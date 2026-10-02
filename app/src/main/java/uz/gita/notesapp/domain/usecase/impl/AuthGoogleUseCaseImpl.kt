package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.usecase.AuthGoogleUseCase
import javax.inject.Inject

class AuthGoogleUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : AuthGoogleUseCase {

    override fun invoke(idToken: String): Flow<Result<Unit>> =
        repository.authGoogle(idToken)
}
