package uz.gita.notesapp.presenter.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.notesapp.presenter.register.RegisterContract.Intent
import uz.gita.notesapp.ui.components.GoogleButton
import uz.gita.notesapp.ui.components.NotesTextField
import uz.gita.notesapp.ui.components.PrimaryButton
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.utils.requestGoogleIdToken

class RegisterScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: RegisterContract.ViewModel = getViewModel<RegisterViewModel>()
        val state = viewModel.collectAsState().value
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val credentialManager = remember { CredentialManager.create(context) }
        val snackbarHostState = remember { SnackbarHostState() }

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is RegisterContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message)
            }
        }

        RegisterScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onEventDispatcher = viewModel::onEventDispatcher,
            onGoogleClick = {
                coroutineScope.launch {
                    requestGoogleIdToken(context, credentialManager)
                        .onSuccess { viewModel.onEventDispatcher(Intent.SignInWithGoogle(it)) }
                        .onFailure { it.message?.let { message -> snackbarHostState.showSnackbar(message) } }
                }
            }
        )
    }
}

@Composable
private fun RegisterScreenContent(
    state: RegisterContract.UiRegisterState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (Intent) -> Unit,
    onGoogleClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val hasError = state.emailError.isNotEmpty() || state.passwordError.isNotEmpty() ||
            state.confirmPasswordError.isNotEmpty()
    val submit = { onEventDispatcher(Intent.Register(email, password, confirmPassword)) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(
                onClick = { onEventDispatcher(Intent.Back) },
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create account",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )
                NotesTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (hasError) onEventDispatcher(Intent.ClearErrors)
                    },
                    placeholder = "Email",
                    leadingIcon = Icons.Outlined.Mail,
                    error = state.emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
                NotesTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (hasError) onEventDispatcher(Intent.ClearErrors)
                    },
                    placeholder = "Password",
                    leadingIcon = Icons.Outlined.Lock,
                    error = state.passwordError,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                NotesTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (hasError) onEventDispatcher(Intent.ClearErrors)
                    },
                    placeholder = "Repeat password",
                    leadingIcon = Icons.Outlined.Lock,
                    error = state.confirmPasswordError,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )
                Spacer(modifier = Modifier.height(4.dp))
                PrimaryButton(text = "Create account", loading = state.loading, onClick = submit)
                GoogleButton(onClick = onGoogleClick, enabled = !state.loading)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRegisterScreenContent() {
    NotesAppTheme {
        RegisterScreenContent(
            state = RegisterContract.UiRegisterState(confirmPasswordError = "Passwords don't match"),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {},
            onGoogleClick = {}
        )
    }
}
