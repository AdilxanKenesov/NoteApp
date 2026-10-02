package uz.gita.notesapp.presenter

import uz.gita.notesapp.presenter.addedit.AddEditContract
import uz.gita.notesapp.presenter.home.HomeContract
import uz.gita.notesapp.presenter.login.LoginContract

class FakeLoginDirections : LoginContract.Directions {
    var openedHome = 0
    override suspend fun navigateToHome() { openedHome++ }
    override suspend fun navigateToRegister() = Unit
}

class FakeAddEditDirections : AddEditContract.Directions {
    var backCount = 0
    override suspend fun back() { backCount++ }
}

class FakeHomeDirections : HomeContract.Directions {
    override suspend fun navigateToProfile() = Unit
    override suspend fun navigateToAddNote() = Unit
    override suspend fun navigateToDetail(id: String) = Unit
}
