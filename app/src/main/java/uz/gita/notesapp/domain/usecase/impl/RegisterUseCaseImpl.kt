package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.usecase.RegisterUseCase
import javax.inject.Inject

class RegisterUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : RegisterUseCase {

    override fun invoke(email: String, password: String): Flow<Result<Unit>> =
        repository.register(email, password)
}
