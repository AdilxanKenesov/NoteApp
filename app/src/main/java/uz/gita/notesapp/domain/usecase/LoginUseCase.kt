package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface LoginUseCase {
    operator fun invoke(email: String, password: String): Flow<Result<Unit>>
}
