package uz.gita.notesapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteImage

interface ImageRepository {
    fun getImages(noteId: String): Flow<List<NoteImage>>
    fun getAllImages(): Flow<List<NoteImage>>
    fun addImages(noteId: String, uris: List<String>): Flow<Result<Unit>>
    fun deleteImage(image: NoteImage): Flow<Result<Unit>>
    suspend fun deleteNoteImages(noteId: String)
    suspend fun deleteAll()
}
