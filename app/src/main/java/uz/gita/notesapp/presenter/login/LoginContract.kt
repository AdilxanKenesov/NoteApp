package uz.gita.notesapp.presenter.login

import org.orbitmvi.orbit.OrbitContainerHost

interface LoginContract {
    interface ViewModel : OrbitContainerHost<UiLoginState, UiLoginState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Login(val email: String, val password: String) : Intent
        data class SignInWithGoogle(val idToken: String) : Intent
        data object ClearErrors : Intent
        data object OpenRegister : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
    }

    data class UiLoginState(
        val loading: Boolean = false,
        val emailError: String = "",
        val passwordError: String = ""
    )

    interface Directions {
        suspend fun navigateToHome()
        suspend fun navigateToRegister()
    }
}
