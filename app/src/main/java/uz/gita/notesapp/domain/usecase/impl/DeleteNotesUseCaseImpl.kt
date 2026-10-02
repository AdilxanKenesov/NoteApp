package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.usecase.DeleteNotesUseCase
import javax.inject.Inject

class DeleteNotesUseCaseImpl @Inject constructor(
    private val notesRepository: NotesRepository,
    private val imageRepository: ImageRepository
) : DeleteNotesUseCase {

    override fun invoke(id: String): Flow<Result<Unit>> = flow {
        notesRepository.deleteNote(id).first().getOrThrow()
        imageRepository.deleteNoteImages(id)
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }
}
