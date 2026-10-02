package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteUIData

interface GetNotesUseCase {
    operator fun invoke(): Flow<Result<List<NoteUIData>>>
}
