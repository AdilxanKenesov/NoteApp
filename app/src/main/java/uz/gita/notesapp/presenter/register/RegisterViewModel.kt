package uz.gita.notesapp.presenter.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.usecase.AuthGoogleUseCase
import uz.gita.notesapp.domain.usecase.RegisterUseCase
import uz.gita.notesapp.presenter.register.RegisterContract.Intent
import uz.gita.notesapp.presenter.register.RegisterContract.SideEffect
import uz.gita.notesapp.presenter.register.RegisterContract.UiRegisterState
import uz.gita.notesapp.utils.isValidEmail
import uz.gita.notesapp.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val directions: RegisterContract.Directions,
    private val registerUseCase: RegisterUseCase,
    private val authGoogleUseCase: AuthGoogleUseCase
) : ViewModel(), RegisterContract.ViewModel {

    override val container: OrbitContainer<UiRegisterState, UiRegisterState, SideEffect> =
        orbitContainer(UiRegisterState())

    override fun onEventDispatcher(intent: Intent) {
        when (intent) {
            is Intent.Register -> register(intent.email, intent.password, intent.confirmPassword)
            is Intent.SignInWithGoogle -> signInWithGoogle(intent.idToken)
            Intent.ClearErrors -> intent {
                reduce { state.copy(emailError = "", passwordError = "", confirmPasswordError = "") }
            }
            Intent.Back -> intent { directions.back() }
        }
    }

    private fun register(email: String, password: String, confirmPassword: String) = intent {
        if (state.loading) return@intent
        val trimmedEmail = email.trim()

        val emailError = when {
            trimmedEmail.isEmpty() -> "Enter your email"
            !trimmedEmail.isValidEmail() -> "This email doesn't look right"
            else -> ""
        }
        val passwordError = when {
            password.isEmpty() -> "Enter a password"
            password.length < 6 -> "At least 6 characters"
            else -> ""
        }
        val confirmPasswordError = when {
            confirmPassword.isEmpty() -> "Repeat the password"
            confirmPassword != password -> "Passwords don't match"
            else -> ""
        }

        reduce {
            state.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }
        if (emailError.isNotEmpty() || passwordError.isNotEmpty() || confirmPasswordError.isNotEmpty()) {
            return@intent
        }

        reduce { state.copy(loading = true) }
        registerUseCase(trimmedEmail, password).collect { result ->
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
