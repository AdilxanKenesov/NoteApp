package uz.gita.notesapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.gita.notesapp.data.source.local.room.AppDatabase
import uz.gita.notesapp.data.source.local.room.ImageDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "notes.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideImageDao(database: AppDatabase): ImageDao = database.imageDao()

    private companion object {
        // v2 dropped reminders; images are kept.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS reminders")
            }
        }
    }
}
