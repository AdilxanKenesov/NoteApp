package uz.gita.notesapp.presenter.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.usecase.AuthGoogleUseCase
import uz.gita.notesapp.domain.usecase.LoginUseCase
import uz.gita.notesapp.presenter.login.LoginContract.Intent
import uz.gita.notesapp.presenter.login.LoginContract.SideEffect
import uz.gita.notesapp.presenter.login.LoginContract.UiLoginState
import uz.gita.notesapp.utils.isValidEmail
import uz.gita.notesapp.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val directions: LoginContract.Directions,
    private val loginUseCase: LoginUseCase,
    private val authGoogleUseCase: AuthGoogleUseCase
) : ViewModel(), LoginContract.ViewModel {

    override val container: OrbitContainer<UiLoginState, UiLoginState, SideEffect> =
        orbitContainer(UiLoginState())

    override fun onEventDispatcher(intent: Intent) {
        when (intent) {
            is Intent.Login -> login(intent.email, intent.password)
            is Intent.SignInWithGoogle -> signInWithGoogle(intent.idToken)
            Intent.ClearErrors -> intent { reduce { state.copy(emailError = "", passwordError = "") } }
            Intent.OpenRegister -> intent { directions.navigateToRegister() }
        }
    }

    private fun login(email: String, password: String) = intent {
        if (state.loading) return@intent
        val trimmedEmail = email.trim()

        // Passwords are not trimmed: Register keeps spaces, so Login must too.
        val emailError = when {
            trimmedEmail.isEmpty() -> "Enter your email"
            !trimmedEmail.isValidEmail() -> "This email doesn't look right"
            else -> ""
        }
        val passwordError = when {
            password.isEmpty() -> "Enter your password"
            password.length < 6 -> "At least 6 characters"
            else -> ""
        }

        reduce { state.copy(emailError = emailError, passwordError = passwordError) }
        if (emailError.isNotEmpty() || passwordError.isNotEmpty()) return@intent

        reduce { state.copy(loading = true) }
        loginUseCase(trimmedEmail, password).collect { result ->
            reduce { state.copy(loading = false) }
            result.onSuccess {
                directions.navigateToHome()
            }.onFailure { throwable ->
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }

    private fun signInWithGoogle(idToken: String) = intent {
        reduce { state.copy(loading = true) }
        authGoogleUseCase(idToken).collect { result ->
            reduce { state.copy(loading = false) }
            result.onSuccess {
                directions.navigateToHome()
            }.onFailure { throwable ->
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }
}
