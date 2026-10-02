package uz.gita.notesapp.data.source.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM images WHERE noteId = :noteId ORDER BY createdAt")
    fun getImages(noteId: String): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images ORDER BY createdAt")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM images WHERE noteId = :noteId")
    suspend fun getImagesOnce(noteId: String): List<ImageEntity>

    @Insert
    suspend fun insert(images: List<ImageEntity>)

    @Delete
    suspend fun delete(image: ImageEntity)

    @Query("DELETE FROM images WHERE noteId = :noteId")
    suspend fun deleteByNote(noteId: String)

    @Query("DELETE FROM images")
    suspend fun deleteAll()
}
