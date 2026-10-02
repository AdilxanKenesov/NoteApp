package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface DeleteAccountUseCase {
    operator fun invoke(password: String?, googleIdToken: String?): Flow<Result<Unit>>
}
