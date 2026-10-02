package uz.gita.notesapp.presenter.register

import uz.gita.notesapp.navigation.AppNavigator
import uz.gita.notesapp.presenter.home.HomeScreen
import javax.inject.Inject

class RegisterDirections @Inject constructor(
    private val navigator: AppNavigator
) : RegisterContract.Directions {

    override suspend fun navigateToHome() {
        navigator.replaceAll(HomeScreen())
    }

    override suspend fun back() {
        navigator.back()
    }
}
