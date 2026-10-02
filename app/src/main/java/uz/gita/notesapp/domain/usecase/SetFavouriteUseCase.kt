package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface SetFavouriteUseCase {
    operator fun invoke(id: String, favourite: Boolean): Flow<Result<Unit>>
}
