package uz.gita.notesapp.data.repository_impl

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import uz.gita.notesapp.data.model.UserData
import uz.gita.notesapp.domain.model.ProfileUIData
import uz.gita.notesapp.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val authFireBase: FirebaseAuth,
    private val credentialManager: CredentialManager
) : AuthRepository {

    override fun isLoggedIn(): Boolean = authFireBase.currentUser != null

    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        authFireBase.addAuthStateListener(listener)

        awaitClose { authFireBase.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override fun login(email: String, password: String): Flow<Result<Unit>> = flow {
        authFireBase.signInWithEmailAndPassword(email, password).await()
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun register(email: String, password: String): Flow<Result<Unit>> = flow {
        val result = authFireBase.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Couldn't create the account")

        try {
            saveUser(user, name = email.substringBefore("@"))
        } catch (e: Exception) {
            // Without the profile document the account is half-created; roll it back
            // so the same email can be used again.
            user.delete().await()
            throw e
        }
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun authGoogle(idToken: String): Flow<Result<Unit>> = flow {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = authFireBase.signInWithCredential(firebaseCredential).await()
        val user = result.user ?: throw Exception("Google sign-in failed")

        val isNewUser = result.additionalUserInfo?.isNewUser == true
        if (isNewUser || !userDocument(user.uid).get().await().exists()) {
            saveUser(user, name = user.displayName.orEmpty())
        }
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun getProfile(): Flow<Result<ProfileUIData>> = flow {
        val user = authFireBase.currentUser ?: throw Exception("You are not signed in")

        val data = userDocument(user.uid).get().await().toObject(UserData::class.java)
        emit(
            Result.success(
                ProfileUIData(
                    name = data?.name?.ifBlank { null } ?: user.displayName.orEmpty(),
                    email = data?.email?.ifBlank { null } ?: user.email.orEmpty(),
                    userImg = data?.userImg ?: user.photoUrl?.toString(),
                    isGoogleAccount = user.isGoogleAccount()
                )
            )
        )
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<Result<Unit>> = flow {
        authFireBase.signOut()
        clearCredentials()
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun deleteAccount(password: String?, googleIdToken: String?): Flow<Result<Unit>> = flow {
        val user = authFireBase.currentUser ?: throw Exception("You are not signed in")

        // Firebase only deletes accounts with a fresh sign-in, so confirm the user first.
        // Nothing is deleted if this step fails.
        val credential = when {
            googleIdToken != null -> GoogleAuthProvider.getCredential(googleIdToken, null)
            !password.isNullOrEmpty() -> EmailAuthProvider.getCredential(user.email.orEmpty(), password)
            else -> throw Exception("Confirm your password")
        }
        user.reauthenticate(credential).await()

        val userDocument = userDocument(user.uid)
        val notes = userDocument.collection("notes").get().await()
        notes.documents.chunked(BATCH_LIMIT).forEach { chunk ->
            fireStore.runBatch { batch -> chunk.forEach { batch.delete(it.reference) } }.await()
        }
        userDocument.delete().await()
        user.delete().await()

        clearCredentials()
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    private suspend fun saveUser(user: FirebaseUser, name: String) {
        val userData = UserData(
            id = user.uid,
            name = name,
            email = user.email.orEmpty(),
            userImg = user.photoUrl?.toString()
        )
        userDocument(user.uid).set(userData).await()
    }

    private suspend fun clearCredentials() {
        runCatching { credentialManager.clearCredentialState(ClearCredentialStateRequest()) }
    }

    private fun userDocument(uid: String) = fireStore.collection("users").document(uid)

    private fun FirebaseUser.isGoogleAccount(): Boolean =
        providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

    private companion object {
        const val BATCH_LIMIT = 450
    }
}
