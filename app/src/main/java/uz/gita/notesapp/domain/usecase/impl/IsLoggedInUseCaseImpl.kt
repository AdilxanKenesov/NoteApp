package uz.gita.notesapp.domain.usecase.impl

import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.usecase.IsLoggedInUseCase
import javax.inject.Inject

class IsLoggedInUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : IsLoggedInUseCase {

    override fun invoke(): Boolean =
        repository.isLoggedIn()
}
