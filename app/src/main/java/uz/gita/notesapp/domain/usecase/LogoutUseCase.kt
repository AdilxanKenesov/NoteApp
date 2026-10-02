package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface LogoutUseCase {
    operator fun invoke(): Flow<Result<Unit>>
}
