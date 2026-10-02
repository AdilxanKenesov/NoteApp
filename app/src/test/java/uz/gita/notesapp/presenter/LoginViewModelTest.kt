package uz.gita.notesapp.presenter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.test
import uz.gita.notesapp.domain.usecase.AuthGoogleUseCase
import uz.gita.notesapp.domain.usecase.LoginUseCase
import uz.gita.notesapp.presenter.login.LoginContract.Intent
import uz.gita.notesapp.presenter.login.LoginViewModel

class LoginViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private val calls = mutableListOf<Pair<String, String>>()
    private val loginUseCase = object : LoginUseCase {
        override fun invoke(email: String, password: String): Flow<Result<Unit>> {
            calls += email to password
            return flowOf(Result.success(Unit))
        }
    }
    private val googleUseCase = object : AuthGoogleUseCase {
        override fun invoke(idToken: String): Flow<Result<Unit>> = flowOf(Result.success(Unit))
    }
    private val directions = FakeLoginDirections()

    @Test
    fun `empty fields show errors and do not call login`() = runTest {
        val viewModel = LoginViewModel(directions, loginUseCase, googleUseCase)
        viewModel.test(this) {
            viewModel.onEventDispatcher(Intent.Login("", ""))
            expectState { copy(emailError = "Enter your email", passwordError = "Enter your password") }
        }
        assertEquals(0, calls.size)
    }

    @Test
    fun `password is sent as typed, email is trimmed`() = runTest {
        val viewModel = LoginViewModel(directions, loginUseCase, googleUseCase)
        viewModel.test(this) {
            viewModel.onEventDispatcher(Intent.Login("  me@mail.com ", " secret1 "))
            expectState { copy(loading = true) }
            expectState { copy(loading = false) }
        }
        assertEquals(listOf("me@mail.com" to " secret1 "), calls)
        assertEquals(1, directions.openedHome)
    }
}
