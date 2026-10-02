package uz.gita.notesapp.presenter.splash

import org.orbitmvi.orbit.OrbitContainerHost

interface SplashContract {
    interface ViewModel : OrbitContainerHost<UiSplashState, UiSplashState, SideEffect>

    sealed interface SideEffect

    data class UiSplashState(
        val ready: Boolean = false
    )

    interface Directions {
        suspend fun navigateToHome()
        suspend fun navigateToLogin()
    }
}
