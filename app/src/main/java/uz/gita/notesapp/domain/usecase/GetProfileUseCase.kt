package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.ProfileUIData

interface GetProfileUseCase {
    operator fun invoke(): Flow<Result<ProfileUIData>>
}
