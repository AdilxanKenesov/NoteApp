package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.usecase.GetAllImagesUseCase
import javax.inject.Inject

class GetAllImagesUseCaseImpl @Inject constructor(
    private val repository: ImageRepository
) : GetAllImagesUseCase {

    override fun invoke(): Flow<List<NoteImage>> =
        repository.getAllImages()
}
