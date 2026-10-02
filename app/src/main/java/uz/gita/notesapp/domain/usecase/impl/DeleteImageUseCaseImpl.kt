package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.usecase.DeleteImageUseCase
import javax.inject.Inject

class DeleteImageUseCaseImpl @Inject constructor(
    private val repository: ImageRepository
) : DeleteImageUseCase {

    override fun invoke(image: NoteImage): Flow<Result<Unit>> =
        repository.deleteImage(image)
}
