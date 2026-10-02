package uz.gita.notesapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.ProfileUIData

interface AuthRepository {
    fun isLoggedIn(): Boolean
    fun observeAuthState(): Flow<Boolean>
    fun login(email: String, password: String): Flow<Result<Unit>>
    fun register(email: String, password: String): Flow<Result<Unit>>
    fun authGoogle(idToken: String): Flow<Result<Unit>>
    fun getProfile(): Flow<Result<ProfileUIData>>
    fun logout(): Flow<Result<Unit>>
    fun deleteAccount(password: String?, googleIdToken: String?): Flow<Result<Unit>>
}
