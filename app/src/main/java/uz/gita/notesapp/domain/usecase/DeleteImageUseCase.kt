package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage

interface DeleteImageUseCase {
    operator fun invoke(image: NoteImage): Flow<Result<Unit>>
}
