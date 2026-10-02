package uz.gita.notesapp.data.repository_impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import uz.gita.notesapp.data.source.local.room.ImageDao
import uz.gita.notesapp.data.source.local.room.ImageEntity
import uz.gita.notesapp.data.storage.ImageStorage
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.repository.ImageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao,
    private val imageStorage: ImageStorage
) : ImageRepository {

    override fun getImages(noteId: String): Flow<List<NoteImage>> =
        imageDao.getImages(noteId).map { list -> list.map { it.toNoteImage() } }

    override fun getAllImages(): Flow<List<NoteImage>> =
        imageDao.getAllImages().map { list -> list.map { it.toNoteImage() } }

    override fun addImages(noteId: String, uris: List<String>): Flow<Result<Unit>> = flow {
        val now = System.currentTimeMillis()
        val entities = uris.mapIndexed { index, uri ->
            ImageEntity(
                noteId = noteId,
                path = imageStorage.save(noteId, uri).absolutePath,
                createdAt = now + index
            )
        }
        imageDao.insert(entities)
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override fun deleteImage(image: NoteImage): Flow<Result<Unit>> = flow {
        imageDao.delete(ImageEntity(image.id, image.noteId, image.path, 0))
        imageStorage.delete(image.path)
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteNoteImages(noteId: String) = withContext(Dispatchers.IO) {
        imageDao.deleteByNote(noteId)
        imageStorage.deleteNote(noteId)
    }

    override suspend fun deleteAll() = withContext(Dispatchers.IO) {
        imageDao.deleteAll()
        imageStorage.deleteAll()
    }

    private fun ImageEntity.toNoteImage() = NoteImage(id = id, noteId = noteId, path = path)
}
