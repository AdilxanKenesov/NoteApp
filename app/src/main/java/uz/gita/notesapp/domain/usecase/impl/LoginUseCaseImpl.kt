package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.usecase.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : LoginUseCase {

    override fun invoke(email: String, password: String): Flow<Result<Unit>> =
        repository.login(email, password)
}
