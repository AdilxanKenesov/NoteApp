package uz.gita.notesapp.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}
