package uz.gita.notesapp.data.repository_impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import uz.gita.notesapp.data.model.NotesData
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.repository.NotesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val authFireBase: FirebaseAuth
) : NotesRepository {

    // Live list: Firestore pushes every change (this device, another device, offline cache).
    override fun getNotes(): Flow<Result<List<NoteUIData>>> = callbackFlow {
        val registration = notesCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val notes = snapshot?.toObjects(NotesData::class.java).orEmpty().map { it.toUIData() }
                trySend(Result.success(notes))
            }

        awaitClose { registration.remove() }
    }.catch {
        emit(Result.failure(it))
    }

    override fun getNote(id: String): Flow<Result<NoteUIData>> = callbackFlow {
        val registration = notesCollection()
            .document(id)
            .addSnapshotListener(Dispatchers.IO.asExecutor()) { snapshot, error ->
                val note = snapshot?.toObject(NotesData::class.java)
                when {
                    error != null -> trySend(Result.failure(error))
                    note == null -> trySend(Result.failure(Exception("Note not found")))
                    else -> trySend(Result.success(note.toUIData()))
                }
            }

        awaitClose { registration.remove() }
    }.catch {
        emit(Result.failure(it))
    }

    override fun addNote(
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean
    ): Flow<Result<String>> = flow {
        val document = notesCollection().document()

        val notesData = NotesData(
            id = document.id,
            title = title,
            description = description,
            type = type.name,
            favourite = favourite,
            createdAt = System.currentTimeMillis()
        )

        document.set(notesData).await()
        emit(Result.success(document.id))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun updateNote(
        id: String,
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean
    ): Flow<Result<Unit>> = flow {
        notesCollection()
            .document(id)
            .update(
                mapOf(
                    "title" to title,
                    "description" to description,
                    "type" to type.name,
                    "favourite" to favourite
                )
            )
            .await()

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun deleteNote(id: String): Flow<Result<Unit>> = flow {
        notesCollection()
            .document(id)
            .delete()
            .await()

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun setFavourite(id: String, favourite: Boolean): Flow<Result<Unit>> = flow {
        notesCollection()
            .document(id)
            .update("favourite", favourite)
            .await()

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    private fun notesCollection(): CollectionReference {
        val uid = authFireBase.currentUser?.uid
            ?: throw Exception("You are not signed in")

        return fireStore.collection("users").document(uid).collection("notes")
    }

    private fun NotesData.toUIData(): NoteUIData = NoteUIData(
        id = id,
        title = title,
        description = description,
        type = NoteType.from(type),
        favourite = favourite,
        createdAt = createdAt
    )
}
