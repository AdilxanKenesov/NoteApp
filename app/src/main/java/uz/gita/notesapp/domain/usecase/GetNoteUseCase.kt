package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteUIData

interface GetNoteUseCase {
    operator fun invoke(id: String): Flow<Result<NoteUIData>>
}
