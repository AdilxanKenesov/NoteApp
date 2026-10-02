package uz.gita.notesapp.presenter.splash

import uz.gita.notesapp.navigation.AppNavigator
import uz.gita.notesapp.presenter.home.HomeScreen
import uz.gita.notesapp.presenter.login.LoginScreen
import javax.inject.Inject

class SplashDirections @Inject constructor(
    private val navigator: AppNavigator
) : SplashContract.Directions {

    override suspend fun navigateToHome() {
        navigator.replaceAll(HomeScreen())
    }

    override suspend fun navigateToLogin() {
        navigator.replaceAll(LoginScreen())
    }
}
