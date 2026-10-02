package uz.gita.notesapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.notesapp.data.repository_impl.AuthRepositoryImpl
import uz.gita.notesapp.data.repository_impl.ImageRepositoryImpl
import uz.gita.notesapp.data.repository_impl.NotesRepositoryImpl
import uz.gita.notesapp.data.repository_impl.SettingsRepositoryImpl
import uz.gita.notesapp.domain.repository.AuthRepository
import uz.gita.notesapp.domain.repository.ImageRepository
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindNotesRepository(impl: NotesRepositoryImpl): NotesRepository

    @Singleton
    @Binds
    fun bindImageRepository(impl: ImageRepositoryImpl): ImageRepository

    @Singleton
    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
