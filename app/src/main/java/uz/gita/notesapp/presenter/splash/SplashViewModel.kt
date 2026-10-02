package uz.gita.notesapp.presenter.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.usecase.IsLoggedInUseCase
import uz.gita.notesapp.presenter.splash.SplashContract.SideEffect
import uz.gita.notesapp.presenter.splash.SplashContract.UiSplashState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val directions: SplashContract.Directions,
    private val isLoggedInUseCase: IsLoggedInUseCase
) : ViewModel(), SplashContract.ViewModel {

    override val container: OrbitContainer<UiSplashState, UiSplashState, SideEffect> =
        orbitContainer(UiSplashState()) {
            if (isLoggedInUseCase()) {
                directions.navigateToHome()
            } else {
                directions.navigateToLogin()
            }
            reduce { state.copy(ready = true) }
        }
}
