package uz.gita.notesapp.presenter.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import uz.gita.notesapp.presenter.login.LoginContract.Intent
import uz.gita.notesapp.ui.components.AppLogo
import uz.gita.notesapp.ui.components.GoogleButton
import uz.gita.notesapp.ui.components.NotesTextField
import uz.gita.notesapp.ui.components.PrimaryButton
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.ui.theme.NotesTheme
import uz.gita.notesapp.utils.requestGoogleIdToken

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: LoginContract.ViewModel = getViewModel<LoginViewModel>()
        val state = viewModel.collectAsState().value
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val credentialManager = remember { CredentialManager.create(context) }
        val snackbarHostState = remember { SnackbarHostState() }

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is LoginContract.SideEffect.ShowMessage -> snackbarHostState.showSnackbar(sideEffect.message)
            }
        }

        LoginScreenContent(
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
private fun LoginScreenContent(
    state: LoginContract.UiLoginState,
    snackbarHostState: SnackbarHostState,
    onEventDispatcher: (Intent) -> Unit,
    onGoogleClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            AppLogo(size = 56.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Sign in", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(28.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NotesTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (state.emailError.isNotEmpty()) onEventDispatcher(Intent.ClearErrors)
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
                        if (state.passwordError.isNotEmpty()) onEventDispatcher(Intent.ClearErrors)
                    },
                    placeholder = "Password",
                    leadingIcon = Icons.Outlined.Lock,
                    error = state.passwordError,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onEventDispatcher(Intent.Login(email, password)) })
                )
                Spacer(modifier = Modifier.height(4.dp))
                PrimaryButton(
                    text = "Sign in",
                    loading = state.loading,
                    onClick = { onEventDispatcher(Intent.Login(email, password)) }
                )
                GoogleButton(onClick = onGoogleClick, enabled = !state.loading)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NotesTheme.colors.muted
                )
                TextButton(onClick = { onEventDispatcher(Intent.OpenRegister) }) {
                    Text(text = "Create one", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLoginScreenContent() {
    NotesAppTheme {
        LoginScreenContent(
            state = LoginContract.UiLoginState(),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {},
            onGoogleClick = {}
        )
    }
}
