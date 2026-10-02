package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.ProfileUIData
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.usecase.GetProfileUseCase
import javax.inject.Inject

class GetProfileUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : GetProfileUseCase {

    override fun invoke(): Flow<Result<ProfileUIData>> =
        repository.getProfile()
}
