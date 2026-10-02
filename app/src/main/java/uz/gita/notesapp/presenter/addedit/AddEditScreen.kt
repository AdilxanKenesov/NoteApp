package uz.gita.notesapp.presenter.addedit

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.presenter.addedit.AddEditContract.Companion.MAX_IMAGES
import uz.gita.notesapp.presenter.addedit.AddEditContract.Intent
import uz.gita.notesapp.ui.components.ImageThumb
import uz.gita.notesapp.ui.components.TypeDot
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.ui.theme.NotesTheme
import java.io.File
import java.util.UUID

data class AddEditScreen(private val noteId: String?) : Screen {

    @Composable
    override fun Content() {
        val viewModel: AddEditContract.ViewModel = getViewModel<AddEditViewModel>()
        val state = viewModel.collectAsState().value
        val snackbarHostState = remember { SnackbarHostState() }
        var confirmDiscard by remember { mutableStateOf(false) }

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddEditContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message)
                AddEditContract.SideEffect.ConfirmDiscard -> confirmDiscard = true
            }
        }

        LaunchedEffect(noteId) {
            viewModel.onEventDispatcher(Intent.Init(noteId))
        }

        BackHandler { viewModel.onEventDispatcher(Intent.Back()) }

        AddEditScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onEventDispatcher = viewModel::onEventDispatcher
        )

        if (confirmDiscard) {
            AlertDialog(
                onDismissRequest = { confirmDiscard = false },
                title = { Text(text = "Discard changes?") },
                confirmButton = {
                    TextButton(onClick = {
                        confirmDiscard = false
                        viewModel.onEventDispatcher(Intent.Back(discard = true))
                    }) { Text(text = "Discard", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmDiscard = false }) { Text(text = "Keep editing") }
                }
            )
        }
    }
}

@Composable
internal fun AddEditScreenContent(
    state: AddEditContract.UiAddEditState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (Intent) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showTypeMenu by remember { mutableStateOf(false) }
    var cameraUri by rememberSaveable { mutableStateOf<String?>(null) }
    val freeSlots = (MAX_IMAGES - state.imageCount).coerceAtLeast(0)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGES)
    ) { uris ->
        if (uris.isNotEmpty()) onEventDispatcher(Intent.AddImages(uris.map(Uri::toString)))
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        val uri = cameraUri
        if (saved && uri != null) onEventDispatcher(Intent.AddImages(listOf(uri)))
        cameraUri = null
    }

    fun openGallery() {
        if (freeSlots == 0) {
            scope.launch { snackbarHostState.showSnackbar("Up to $MAX_IMAGES images per note") }
            return
        }
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun openCamera() {
        if (freeSlots == 0) {
            scope.launch { snackbarHostState.showSnackbar("Up to $MAX_IMAGES images per note") }
            return
        }
        val dir = File(context.cacheDir, "camera").apply { mkdirs() }
        val file = File(dir, "${UUID.randomUUID()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        cameraUri = uri.toString()
        cameraLauncher.launch(uri)
    }


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
                IconButton(onClick = { onEventDispatcher(Intent.Back()) }) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onEventDispatcher(Intent.Save) },
                    enabled = !state.saving,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (state.saving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = "Save")
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Box {
                    ToolbarButton(Icons.AutoMirrored.Outlined.Label, "Type") { showTypeMenu = true }
                    DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                        NoteType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type.title) },
                                leadingIcon = { TypeDot(color = NotesTheme.colors.typeColor(type)) },
                                trailingIcon = if (type == state.type) {
                                    { Icon(imageVector = Icons.Outlined.Check, contentDescription = null) }
                                } else null,
                                onClick = {
                                    showTypeMenu = false
                                    onEventDispatcher(Intent.ChangeType(type))
                                }
                            )
                        }
                    }
                }
                ToolbarButton(Icons.Outlined.Image, "Add images", onClick = ::openGallery)
                ToolbarButton(Icons.Outlined.PhotoCamera, "Take a photo", onClick = ::openCamera)
                ToolbarButton(
                    icon = if (state.favourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    description = "Favourite",
                    tint = if (state.favourite) NotesTheme.colors.star else null
                ) { onEventDispatcher(Intent.ToggleFavourite) }
            }
        }
    ) { padding ->
        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PlainField(
                value = state.title,
                onValueChange = { onEventDispatcher(Intent.ChangeTitle(it)) },
                placeholder = "Title",
                textStyle = MaterialTheme.typography.headlineSmall
            )
            if (state.titleError.isNotEmpty()) {
                Text(
                    text = state.titleError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 0.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable { showTypeMenu = true }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    TypeDot(color = NotesTheme.colors.typeColor(state.type))
                    Text(text = state.type.title, style = MaterialTheme.typography.labelMedium, color = NotesTheme.colors.muted)
                }
            }

            PlainField(
                value = state.description,
                onValueChange = { onEventDispatcher(Intent.ChangeDescription(it)) },
                placeholder = "Write something…",
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = false,
                minHeight = 160
            )

            if (state.imageCount > 0) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.images.forEach { image ->
                        RemovableImage(path = image.path) { onEventDispatcher(Intent.RemoveImage(image)) }
                    }
                    state.newImages.forEach { uri ->
                        RemovableImage(path = uri) { onEventDispatcher(Intent.RemoveNewImage(uri)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlainField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    singleLine: Boolean = false,
    minHeight: Int = 0
) {
    val color = MaterialTheme.colorScheme.onBackground
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = color),
        singleLine = singleLine,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (minHeight > 0) Modifier.height(minHeight.dp) else Modifier),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text(text = placeholder, style = textStyle, color = NotesTheme.colors.faint)
                }
                inner()
            }
        }
    )
}

@Composable
private fun RemovableImage(path: String, onRemove: () -> Unit) {
    Box {
        ImageThumb(path = path, size = 76.dp)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Remove image",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ToolbarButton(
    icon: ImageVector,
    description: String,
    active: Boolean = false,
    tint: Color? = null,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = tint ?: if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(imageVector = icon, contentDescription = description)
    }
}

@Preview
@Composable
private fun PreviewAddEditScreenContent() {
    NotesAppTheme {
        AddEditScreenContent(
            state = AddEditContract.UiAddEditState(
                initialized = true,
                title = "Retrofit timeouts",
                description = "Requests time out after 10 seconds on slow networks."
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {}
        )
    }
}
