package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface DeleteNotesUseCase {
    operator fun invoke(id: String): Flow<Result<Unit>>
}
