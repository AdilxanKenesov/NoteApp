package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.usecase.AddNotesUseCase
import javax.inject.Inject

class AddNotesUseCaseImpl @Inject constructor(
    private val notesRepository: NotesRepository,
    private val imageRepository: ImageRepository
) : AddNotesUseCase {

    override fun invoke(
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean,
        imageUris: List<String>
    ): Flow<Result<String>> = flow {
        val id = notesRepository
            .addNote(title, description, type, favourite)
            .first()
            .getOrThrow()

        if (imageUris.isNotEmpty()) {
            imageRepository.addImages(id, imageUris).first().getOrThrow()
        }

        emit(Result.success(id))
    }.catch {
        emit(Result.failure(it))
    }
}
