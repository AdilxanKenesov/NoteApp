package uz.gita.notesapp.data.source.local.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "images", indices = [Index("noteId")])
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: String,
    val path: String,
    val createdAt: Long
)
