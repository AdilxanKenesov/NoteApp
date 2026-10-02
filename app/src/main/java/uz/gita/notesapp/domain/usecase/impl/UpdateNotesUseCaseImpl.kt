package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.usecase.UpdateNotesUseCase
import javax.inject.Inject

class UpdateNotesUseCaseImpl @Inject constructor(
    private val notesRepository: NotesRepository,
    private val imageRepository: ImageRepository
) : UpdateNotesUseCase {

    override fun invoke(
        id: String,
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean,
        newImageUris: List<String>
    ): Flow<Result<Unit>> = flow {
        notesRepository
            .updateNote(id, title, description, type, favourite)
            .first()
            .getOrThrow()

        if (newImageUris.isNotEmpty()) {
            imageRepository.addImages(id, newImageUris).first().getOrThrow()
        }

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }
}
