package uz.gita.notesapp.presenter.register

import org.orbitmvi.orbit.OrbitContainerHost

interface RegisterContract {
    interface ViewModel : OrbitContainerHost<UiRegisterState, UiRegisterState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Register(
            val email: String,
            val password: String,
            val confirmPassword: String
        ) : Intent

        data class SignInWithGoogle(val idToken: String) : Intent
        data object ClearErrors : Intent
        data object Back : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
    }

    data class UiRegisterState(
        val loading: Boolean = false,
        val emailError: String = "",
        val passwordError: String = "",
        val confirmPasswordError: String = ""
    )

    interface Directions {
        suspend fun navigateToHome()
        suspend fun back()
    }
}
