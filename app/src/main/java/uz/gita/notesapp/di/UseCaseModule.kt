package uz.gita.notesapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.notesapp.domain.usecase.AddNotesUseCase
import uz.gita.notesapp.domain.usecase.AuthGoogleUseCase
import uz.gita.notesapp.domain.usecase.DeleteAccountUseCase
import uz.gita.notesapp.domain.usecase.DeleteNotesUseCase
import uz.gita.notesapp.domain.usecase.GetNoteUseCase
import uz.gita.notesapp.domain.usecase.GetNotesUseCase
import uz.gita.notesapp.domain.usecase.GetProfileUseCase
import uz.gita.notesapp.domain.usecase.IsLoggedInUseCase
import uz.gita.notesapp.domain.usecase.LoginUseCase
import uz.gita.notesapp.domain.usecase.LogoutUseCase
import uz.gita.notesapp.domain.usecase.RegisterUseCase
import uz.gita.notesapp.domain.usecase.SetFavouriteUseCase
import uz.gita.notesapp.domain.usecase.UpdateNotesUseCase
import uz.gita.notesapp.domain.usecase.impl.AddNotesUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.AuthGoogleUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.DeleteAccountUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.DeleteNotesUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.GetNoteUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.GetNotesUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.GetProfileUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.IsLoggedInUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.LoginUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.LogoutUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.RegisterUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.SetFavouriteUseCaseImpl
import uz.gita.notesapp.domain.usecase.impl.UpdateNotesUseCaseImpl
import uz.gita.notesapp.domain.usecase.GetNoteImagesUseCase
import uz.gita.notesapp.domain.usecase.impl.GetNoteImagesUseCaseImpl
import uz.gita.notesapp.domain.usecase.GetAllImagesUseCase
import uz.gita.notesapp.domain.usecase.impl.GetAllImagesUseCaseImpl
import uz.gita.notesapp.domain.usecase.DeleteImageUseCase
import uz.gita.notesapp.domain.usecase.impl.DeleteImageUseCaseImpl
import uz.gita.notesapp.domain.usecase.GetThemeUseCase
import uz.gita.notesapp.domain.usecase.impl.GetThemeUseCaseImpl
import uz.gita.notesapp.domain.usecase.SetThemeUseCase
import uz.gita.notesapp.domain.usecase.impl.SetThemeUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Singleton
    @Binds
    fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase

    @Singleton
    @Binds
    fun bindRegisterUseCase(impl: RegisterUseCaseImpl): RegisterUseCase

    @Singleton
    @Binds
    fun bindAuthGoogleUseCase(impl: AuthGoogleUseCaseImpl): AuthGoogleUseCase

    @Singleton
    @Binds
    fun bindGetProfileUseCase(impl: GetProfileUseCaseImpl): GetProfileUseCase

    @Singleton
    @Binds
    fun bindLogoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase

    @Singleton
    @Binds
    fun bindDeleteAccountUseCase(impl: DeleteAccountUseCaseImpl): DeleteAccountUseCase

    @Singleton
    @Binds
    fun bindIsLoggedInUseCase(impl: IsLoggedInUseCaseImpl): IsLoggedInUseCase

    @Singleton
    @Binds
    fun bindGetNotesUseCase(impl: GetNotesUseCaseImpl): GetNotesUseCase

    @Singleton
    @Binds
    fun bindGetNoteUseCase(impl: GetNoteUseCaseImpl): GetNoteUseCase

    @Singleton
    @Binds
    fun bindAddNotesUseCase(impl: AddNotesUseCaseImpl): AddNotesUseCase

    @Singleton
    @Binds
    fun bindUpdateNotesUseCase(impl: UpdateNotesUseCaseImpl): UpdateNotesUseCase

    @Singleton
    @Binds
    fun bindDeleteNotesUseCase(impl: DeleteNotesUseCaseImpl): DeleteNotesUseCase

    @Singleton
    @Binds
    fun bindSetFavouriteUseCase(impl: SetFavouriteUseCaseImpl): SetFavouriteUseCase

    @Singleton
    @Binds
    fun bindGetNoteImagesUseCase(impl: GetNoteImagesUseCaseImpl): GetNoteImagesUseCase

    @Singleton
    @Binds
    fun bindGetAllImagesUseCase(impl: GetAllImagesUseCaseImpl): GetAllImagesUseCase

    @Singleton
    @Binds
    fun bindDeleteImageUseCase(impl: DeleteImageUseCaseImpl): DeleteImageUseCase

    @Singleton
    @Binds
    fun bindGetThemeUseCase(impl: GetThemeUseCaseImpl): GetThemeUseCase

    @Singleton
    @Binds
    fun bindSetThemeUseCase(impl: SetThemeUseCaseImpl): SetThemeUseCase
}
