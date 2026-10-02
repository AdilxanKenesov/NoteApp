package uz.gita.notesapp.presenter.login

import uz.gita.notesapp.navigation.AppNavigator
import uz.gita.notesapp.presenter.home.HomeScreen
import uz.gita.notesapp.presenter.register.RegisterScreen
import javax.inject.Inject

class LoginDirections @Inject constructor(
    private val navigator: AppNavigator
) : LoginContract.Directions {

    override suspend fun navigateToHome() {
        navigator.replaceAll(HomeScreen())
    }

    override suspend fun navigateToRegister() {
        navigator.navigateTo(RegisterScreen())
    }
}
