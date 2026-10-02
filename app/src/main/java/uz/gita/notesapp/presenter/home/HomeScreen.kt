package uz.gita.notesapp.presenter.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.presenter.home.HomeContract.HomeFilter
import uz.gita.notesapp.presenter.home.HomeContract.Intent
import uz.gita.notesapp.ui.components.Avatar
import uz.gita.notesapp.ui.components.EmptyState
import uz.gita.notesapp.ui.components.NoteCard
import uz.gita.notesapp.ui.components.TypeDot
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.ui.theme.NotesTheme

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: HomeContract.ViewModel = getViewModel<HomeViewModel>()
        val state = viewModel.collectAsState().value
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is HomeContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message)
                // Launched so a second swipe replaces this snackbar instead of queueing
                // behind it (the ViewModel's undo window starts right away).
                is HomeContract.SideEffect.ShowUndo -> scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = "Note deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEventDispatcher(Intent.UndoDelete(sideEffect.id))
                    }
                }
            }
        }

        HomeScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onEventDispatcher = viewModel::onEventDispatcher
        )
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeContract.UiHomeState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (Intent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEventDispatcher(Intent.OpenAddNote) },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "New note", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = "header") {
                    Header(state = state, onEventDispatcher = onEventDispatcher)
                }

                items(items = state.notes, key = { it.id }) { note ->
                    SwipeToDeleteBox(
                        onDelete = { onEventDispatcher(Intent.Delete(note.id)) },
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                            .animateItem()
                    ) {
                        NoteCard(
                            note = note,
                            image = state.firstImages[note.id],
                            onClick = { onEventDispatcher(Intent.OpenDetail(note.id)) },
                            onFavourite = { onEventDispatcher(Intent.ToggleFavourite(note.id, !note.favourite)) }
                        )
                    }
                }
            }

            when {
                state.loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.allNotes.all { it.id in state.pendingDelete } -> EmptyState(text = "No notes yet", modifier = Modifier.align(Alignment.Center))
                state.notes.isEmpty() -> Text(
                    text = "Nothing found",
                    style = MaterialTheme.typography.titleSmall,
                    color = NotesTheme.colors.muted,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun SwipeToDeleteBox(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Plain remember, not rememberSaveable: the lazy list keeps saved state per key,
    // so an undone note would come back still swiped away.
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    val state = remember { SwipeToDismissBoxState(SwipeToDismissBoxValue.Settled, positionalThreshold) }

    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        onDismiss = { onDelete() },
        backgroundContent = {
            val alignment = if (state.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                Alignment.CenterStart
            } else {
                Alignment.CenterEnd
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun Header(
    state: HomeContract.UiHomeState,
    onEventDispatcher: (Intent) -> Unit
) = Column {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 12.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onEventDispatcher(Intent.OpenProfile) }) {
            Avatar(name = state.userName, imageUrl = state.userImg, size = 38.dp)
        }
    }

    TextField(
        value = state.query,
        onValueChange = { onEventDispatcher(Intent.Search(it)) },
        placeholder = { Text(text = "Search") },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = if (state.query.isNotEmpty()) {
            {
                IconButton(onClick = { onEventDispatcher(Intent.Search("")) }) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear search")
                }
            }
        } else null,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = NotesTheme.colors.surface2,
            unfocusedContainerColor = NotesTheme.colors.surface2,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedPlaceholderColor = NotesTheme.colors.faint,
            focusedPlaceholderColor = NotesTheme.colors.faint,
            unfocusedLeadingIconColor = NotesTheme.colors.faint,
            focusedLeadingIconColor = NotesTheme.colors.faint
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    )

    val filters = buildList {
        add(HomeFilter.All)
        add(HomeFilter.Favourites)
        NoteType.entries.forEach { add(HomeFilter.Type(it)) }
    }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        items(filters) { filter ->
            FilterPill(
                filter = filter,
                selected = state.filter == filter,
                onClick = { onEventDispatcher(Intent.SelectFilter(filter)) }
            )
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
private fun FilterPill(filter: HomeFilter, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surfaceContainer
    val content = if (selected) MaterialTheme.colorScheme.background else NotesTheme.colors.muted

    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        when (filter) {
            HomeFilter.All -> Text(text = "All", style = MaterialTheme.typography.labelMedium, color = content)
            HomeFilter.Favourites -> FilterIcon(Icons.Outlined.StarBorder, "Favourites", if (selected) content else NotesTheme.colors.star)
            is HomeFilter.Type -> {
                TypeDot(color = NotesTheme.colors.typeColor(filter.type))
                Text(text = filter.type.title, style = MaterialTheme.typography.labelMedium, color = content)
            }
        }
    }
}

@Composable
private fun FilterIcon(icon: ImageVector, description: String, tint: Color) {
    Icon(imageVector = icon, contentDescription = description, tint = tint, modifier = Modifier.size(16.dp))
}

@Preview
@Composable
private fun PreviewHomeScreenContent() {
    NotesAppTheme {
        HomeScreenContent(
            state = HomeContract.UiHomeState(
                loading = false,
                userName = "Adilxan Kenesov",
                allNotes = previewNotes,
                notes = previewNotes
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {}
        )
    }
}

private val previewNotes = listOf(
    NoteUIData(
        id = "1",
        title = "Sprint planning",
        description = "Move the release to Friday, ask design for the new icons",
        type = NoteType.WORK,
        favourite = true,
        createdAt = 1_768_000_000_000
    ),
    NoteUIData(
        id = "2",
        title = "Chapter 4 summary",
        description = "Supply and demand, price elasticity",
        type = NoteType.STUDY,
        favourite = false,
        createdAt = 1_768_000_000_000
    )
)
