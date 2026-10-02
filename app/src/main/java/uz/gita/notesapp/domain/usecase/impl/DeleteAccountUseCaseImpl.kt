package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.usecase.DeleteAccountUseCase
import javax.inject.Inject

class DeleteAccountUseCaseImpl @Inject constructor(
    private val repository: AuthRepository,
    private val imageRepository: ImageRepository
) : DeleteAccountUseCase {

    override fun invoke(password: String?, googleIdToken: String?): Flow<Result<Unit>> =
        repository.deleteAccount(password, googleIdToken).onEach { result ->
            if (result.isSuccess) imageRepository.deleteAll()
        }
}
