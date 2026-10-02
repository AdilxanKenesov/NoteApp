package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteType

interface UpdateNotesUseCase {
    operator fun invoke(
        id: String,
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean,
        newImageUris: List<String>
    ): Flow<Result<Unit>>
}
