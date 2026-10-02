package uz.gita.notesapp.presenter.addedit

import uz.gita.notesapp.navigation.AppNavigator
import javax.inject.Inject

class AddEditDirections @Inject constructor(
    private val navigator: AppNavigator
) : AddEditContract.Directions {

    override suspend fun back() {
        navigator.back()
    }
}
