package uz.gita.notesapp.presenter.profile

import uz.gita.notesapp.navigation.AppNavigator
import uz.gita.notesapp.presenter.login.LoginScreen
import javax.inject.Inject

class ProfileDirections @Inject constructor(
    private val navigator: AppNavigator
) : ProfileContract.Directions {

    override suspend fun navigateToLogin() {
        navigator.replaceAll(LoginScreen())
    }

    override suspend fun back() {
        navigator.back()
    }
}
