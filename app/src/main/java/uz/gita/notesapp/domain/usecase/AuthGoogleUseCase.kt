package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface AuthGoogleUseCase {
    operator fun invoke(idToken: String): Flow<Result<Unit>>
}
