package uz.gita.notesapp.presenter.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import coil.compose.AsyncImage
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.presenter.detail.DetailContract.Intent
import uz.gita.notesapp.ui.components.ImageThumb
import uz.gita.notesapp.ui.components.TypeLabel
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.ui.theme.NotesTheme
import uz.gita.notesapp.utils.toReadableDate
import java.io.File

data class DetailScreen(private val noteId: String) : Screen {

    @Composable
    override fun Content() {
        val viewModel: DetailContract.ViewModel = getViewModel<DetailViewModel>()
        val state = viewModel.collectAsState().value
        val snackbarHostState = remember { SnackbarHostState() }

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is DetailContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message)
            }
        }

        LaunchedEffect(noteId) {
            viewModel.onEventDispatcher(Intent.Init(noteId))
        }

        DetailScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onEventDispatcher = viewModel::onEventDispatcher
        )
    }
}

@Composable
private fun DetailScreenContent(
    state: DetailContract.UiDetailState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (Intent) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var openedImage by rememberSaveable { mutableStateOf<Int?>(null) }
    val note = state.note

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onEventDispatcher(Intent.Back) }) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                if (note != null) {
                    IconButton(onClick = { onEventDispatcher(Intent.ToggleFavourite) }) {
                        Icon(
                            imageVector = if (note.favourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favourite",
                            tint = if (note.favourite) NotesTheme.colors.star else MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Delete", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    confirmDelete = true
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (note != null) {
                ExtendedFloatingActionButton(
                    onClick = { onEventDispatcher(Intent.Edit) },
                    icon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = null) },
                    text = { Text(text = "Edit") },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (note == null || state.deleting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                NoteBody(
                    note = note,
                    images = state.images,
                    onImageClick = { openedImage = it }
                )
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(text = "Delete note?") },
            text = { Text(text = "It will also remove its images.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onEventDispatcher(Intent.Delete)
                }) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(text = "Cancel") }
            }
        )
    }

    openedImage?.let { index ->
        if (index in state.images.indices) {
            ImageViewer(images = state.images, startIndex = index, onClose = { openedImage = null })
        }
    }
}

@Composable
private fun NoteBody(
    note: NoteUIData,
    images: List<NoteImage>,
    onImageClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TypeLabel(type = note.type, suffix = " · ${note.createdAt.toReadableDate()}")
        Text(text = note.title, style = MaterialTheme.typography.headlineSmall)

        if (note.description.isNotBlank()) {
            SelectionContainer {
                Text(text = note.description, style = MaterialTheme.typography.bodyLarge)
            }
        }

        if (images.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                images.forEachIndexed { index, image ->
                    ImageThumb(path = image.path, size = 96.dp, onClick = { onImageClick(index) })
                }
            }
        }
    }
}

@Composable
private fun ImageViewer(images: List<NoteImage>, startIndex: Int, onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val pagerState = rememberPagerState(initialPage = startIndex) { images.size }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                AsyncImage(
                    model = File(images[page].path),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onClose)
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
            }
            if (images.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${images.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDetailScreenContent() {
    NotesAppTheme {
        DetailScreenContent(
            state = DetailContract.UiDetailState(
                loading = false,
                note = NoteUIData(
                    id = "1",
                    title = "Trip to Samarkand",
                    description = "Bring the boarding pass and charger.",
                    type = NoteType.PERSONAL,
                    favourite = true,
                    createdAt = 1_768_000_000_000
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {}
        )
    }
}
