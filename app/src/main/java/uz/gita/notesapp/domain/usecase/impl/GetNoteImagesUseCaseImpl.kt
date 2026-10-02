package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.usecase.GetNoteImagesUseCase
import javax.inject.Inject

class GetNoteImagesUseCaseImpl @Inject constructor(
    private val repository: ImageRepository
) : GetNoteImagesUseCase {

    override fun invoke(noteId: String): Flow<List<NoteImage>> =
        repository.getImages(noteId)
}
