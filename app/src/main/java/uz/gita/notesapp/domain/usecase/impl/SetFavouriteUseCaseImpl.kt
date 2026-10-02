package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.usecase.SetFavouriteUseCase
import javax.inject.Inject

class SetFavouriteUseCaseImpl @Inject constructor(
    private val repository: NotesRepository
) : SetFavouriteUseCase {

    override fun invoke(id: String, favourite: Boolean): Flow<Result<Unit>> =
        repository.setFavourite(id, favourite)
}
