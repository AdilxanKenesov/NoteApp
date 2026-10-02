package uz.gita.notesapp.presenter.home

import uz.gita.notesapp.navigation.AppNavigator
import uz.gita.notesapp.presenter.addedit.AddEditScreen
import uz.gita.notesapp.presenter.detail.DetailScreen
import uz.gita.notesapp.presenter.profile.ProfileScreen
import javax.inject.Inject

class HomeDirections @Inject constructor(
    private val navigator: AppNavigator
) : HomeContract.Directions {

    override suspend fun navigateToProfile() {
        navigator.navigateTo(ProfileScreen())
    }

    override suspend fun navigateToAddNote() {
        navigator.navigateTo(AddEditScreen(null))
    }

    override suspend fun navigateToDetail(id: String) {
        navigator.navigateTo(DetailScreen(id))
    }
}
