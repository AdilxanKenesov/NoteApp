package uz.gita.notesapp.presenter.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.notesapp.domain.model.ProfileUIData
import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.presenter.profile.ProfileContract.Intent
import uz.gita.notesapp.ui.components.Avatar
import uz.gita.notesapp.ui.components.GoogleButton
import uz.gita.notesapp.ui.components.NotesTextField
import uz.gita.notesapp.ui.components.PrimaryButton
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.ui.theme.NotesTheme
import uz.gita.notesapp.utils.requestGoogleIdToken

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: ProfileContract.ViewModel = getViewModel<ProfileViewModel>()
        val state = viewModel.collectAsState().value
        val snackbarHostState = remember { SnackbarHostState() }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val credentialManager = remember { CredentialManager.create(context) }

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is ProfileContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message)
            }
        }

        ProfileScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onEventDispatcher = viewModel::onEventDispatcher,
            onConfirmWithGoogle = {
                scope.launch {
                    requestGoogleIdToken(context, credentialManager)
                        .onSuccess { viewModel.onEventDispatcher(Intent.DeleteAccount(null, it)) }
                        .onFailure { it.message?.let { message -> snackbarHostState.showSnackbar(message) } }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreenContent(
    state: ProfileContract.UiProfileState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (Intent) -> Unit,
    onConfirmWithGoogle: () -> Unit
) {
    var showDelete by rememberSaveable { mutableStateOf(false) }
    val profile = state.profile

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            IconButton(
                onClick = { onEventDispatcher(Intent.Back) },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Avatar(name = profile?.name.orEmpty(), imageUrl = profile?.userImg, size = 80.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = profile?.name.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = profile?.email.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = NotesTheme.colors.muted
                    )
                }
            }

            Group {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Theme", style = MaterialTheme.typography.titleSmall)
                    ThemeSelector(selected = state.themeMode) { onEventDispatcher(Intent.ChangeTheme(it)) }
                }
            }

            Group {
                SettingRow(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    title = "Sign out",
                    enabled = !state.busy,
                    onClick = { onEventDispatcher(Intent.Logout) }
                )
                HorizontalDivider(color = NotesTheme.colors.line)
                SettingRow(
                    icon = Icons.Outlined.DeleteOutline,
                    title = "Delete account",
                    tint = MaterialTheme.colorScheme.error,
                    enabled = !state.busy,
                    onClick = { showDelete = true }
                )
            }
        }
    }

    if (showDelete && profile != null) {
        DeleteAccountSheet(
            profile = profile,
            busy = state.busy,
            error = state.deleteError,
            onDismiss = { showDelete = false },
            onConfirmPassword = { onEventDispatcher(Intent.DeleteAccount(it, null)) },
            onConfirmWithGoogle = onConfirmWithGoogle
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountSheet(
    profile: ProfileUIData,
    busy: Boolean,
    error: String,
    onDismiss: () -> Unit,
    onConfirmPassword: (String) -> Unit,
    onConfirmWithGoogle: () -> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 18.dp, end = 18.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = "Delete account?", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "All notes and images will be deleted. This can't be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = NotesTheme.colors.muted
            )

            if (profile.isGoogleAccount) {
                GoogleButton(onClick = onConfirmWithGoogle, enabled = !busy)
            } else {
                NotesTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Your password",
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true
                )
                PrimaryButton(
                    text = "Delete",
                    loading = busy,
                    enabled = password.isNotEmpty(),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    onClick = { onConfirmPassword(password) }
                )
            }

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun Group(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        content()
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = tint, modifier = Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Composable
private fun ThemeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NotesTheme.colors.surface2)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        ThemeMode.entries.forEach { mode ->
            val on = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (on) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent)
                    .clickable { onSelect(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (on) MaterialTheme.colorScheme.onSurface else NotesTheme.colors.muted
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProfileScreenContent() {
    NotesAppTheme {
        ProfileScreenContent(
            state = ProfileContract.UiProfileState(
                loading = false,
                profile = ProfileUIData("Adilxan Kenesov", "adilxan@gmail.com", null, isGoogleAccount = false)
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {},
            onConfirmWithGoogle = {}
        )
    }
}
