package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage

interface GetNoteImagesUseCase {
    operator fun invoke(noteId: String): Flow<List<NoteImage>>
}
