package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.usecase.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : LogoutUseCase {

    override fun invoke(): Flow<Result<Unit>> =
        repository.logout()
}
