package uz.gita.notesapp.presenter.profile

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.notesapp.domain.model.ProfileUIData
import uz.gita.notesapp.domain.model.ThemeMode

interface ProfileContract {
    interface ViewModel : OrbitContainerHost<UiProfileState, UiProfileState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class ChangeTheme(val mode: ThemeMode) : Intent
        data object Logout : Intent
        data class DeleteAccount(val password: String?, val googleIdToken: String?) : Intent
        data object Back : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
    }

    data class UiProfileState(
        val loading: Boolean = true,
        val busy: Boolean = false,
        val profile: ProfileUIData? = null,
        val themeMode: ThemeMode = ThemeMode.SYSTEM,
        val deleteError: String = ""
    )

    interface Directions {
        suspend fun navigateToLogin()
        suspend fun back()
    }
}
