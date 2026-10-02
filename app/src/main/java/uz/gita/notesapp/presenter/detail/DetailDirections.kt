package uz.gita.notesapp.presenter.detail

import uz.gita.notesapp.navigation.AppNavigator
import uz.gita.notesapp.presenter.addedit.AddEditScreen
import javax.inject.Inject

class DetailDirections @Inject constructor(
    private val navigator: AppNavigator
) : DetailContract.Directions {

    // Pushed on top of Detail, so after saving the user lands back on the (live) detail.
    override suspend fun navigateToEdit(id: String) {
        navigator.navigateTo(AddEditScreen(id))
    }

    override suspend fun back() {
        navigator.back()
    }
}
