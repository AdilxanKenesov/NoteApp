package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage

interface GetAllImagesUseCase {
    operator fun invoke(): Flow<List<NoteImage>>
}
