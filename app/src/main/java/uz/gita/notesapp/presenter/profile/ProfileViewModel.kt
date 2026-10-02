package uz.gita.notesapp.presenter.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.domain.usecase.DeleteAccountUseCase
import uz.gita.notesapp.domain.usecase.GetProfileUseCase
import uz.gita.notesapp.domain.usecase.GetThemeUseCase
import uz.gita.notesapp.domain.usecase.LogoutUseCase
import uz.gita.notesapp.domain.usecase.SetThemeUseCase
import uz.gita.notesapp.presenter.profile.ProfileContract.Intent
import uz.gita.notesapp.presenter.profile.ProfileContract.SideEffect
import uz.gita.notesapp.presenter.profile.ProfileContract.UiProfileState
import uz.gita.notesapp.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val directions: ProfileContract.Directions,
    private val getProfileUseCase: GetProfileUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel(), ProfileContract.ViewModel {

    override val container: OrbitContainer<UiProfileState, UiProfileState, SideEffect> =
        orbitContainer(UiProfileState()) {
            loadProfile()
            observeTheme()
        }

    override fun onEventDispatcher(intent: Intent) {
        when (intent) {
            is Intent.ChangeTheme -> changeTheme(intent.mode)
            Intent.Logout -> logout()
            is Intent.DeleteAccount -> deleteAccount(intent.password, intent.googleIdToken)
            Intent.Back -> intent { directions.back() }
        }
    }

    private fun loadProfile() = intent {
        getProfileUseCase().collect { result ->
            result.onSuccess { profile ->
                reduce { state.copy(loading = false, profile = profile) }
            }.onFailure { throwable ->
                reduce { state.copy(loading = false) }
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }

    private fun observeTheme() = intent {
        repeatOnSubscription {
            getThemeUseCase().collect { mode -> reduce { state.copy(themeMode = mode) } }
        }
    }

    private fun changeTheme(mode: ThemeMode) = intent {
        setThemeUseCase(mode)
    }

    private fun logout() = intent {
        if (state.busy) return@intent
        reduce { state.copy(busy = true) }
        logoutUseCase().collect { result ->
            reduce { state.copy(busy = false) }
            result.onSuccess {
                directions.navigateToLogin()
            }.onFailure { throwable ->
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }

    private fun deleteAccount(password: String?, googleIdToken: String?) = intent {
        if (state.busy) return@intent
        reduce { state.copy(busy = true, deleteError = "") }
        deleteAccountUseCase(password, googleIdToken).collect { result ->
            result.onSuccess {
                reduce { state.copy(busy = false) }
                directions.navigateToLogin()
            }.onFailure { throwable ->
                reduce { state.copy(busy = false, deleteError = throwable.userMessage()) }
            }
        }
    }
}
