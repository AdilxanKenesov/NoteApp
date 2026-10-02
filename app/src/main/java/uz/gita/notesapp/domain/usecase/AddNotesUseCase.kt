package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteType

interface AddNotesUseCase {
    operator fun invoke(
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean,
        imageUris: List<String>
    ): Flow<Result<String>>
}
