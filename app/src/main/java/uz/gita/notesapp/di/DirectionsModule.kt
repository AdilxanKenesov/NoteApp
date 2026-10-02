package uz.gita.notesapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.notesapp.presenter.addedit.AddEditContract
import uz.gita.notesapp.presenter.addedit.AddEditDirections
import uz.gita.notesapp.presenter.detail.DetailContract
import uz.gita.notesapp.presenter.detail.DetailDirections
import uz.gita.notesapp.presenter.home.HomeContract
import uz.gita.notesapp.presenter.home.HomeDirections
import uz.gita.notesapp.presenter.login.LoginContract
import uz.gita.notesapp.presenter.login.LoginDirections
import uz.gita.notesapp.presenter.profile.ProfileContract
import uz.gita.notesapp.presenter.profile.ProfileDirections
import uz.gita.notesapp.presenter.register.RegisterContract
import uz.gita.notesapp.presenter.register.RegisterDirections
import uz.gita.notesapp.presenter.splash.SplashContract
import uz.gita.notesapp.presenter.splash.SplashDirections
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DirectionsModule {

    @Singleton
    @Binds
    fun bindSplashDirections(impl: SplashDirections): SplashContract.Directions

    @Singleton
    @Binds
    fun bindLoginDirections(impl: LoginDirections): LoginContract.Directions

    @Singleton
    @Binds
    fun bindRegisterDirections(impl: RegisterDirections): RegisterContract.Directions

    @Singleton
    @Binds
    fun bindHomeDirections(impl: HomeDirections): HomeContract.Directions

    @Singleton
    @Binds
    fun bindDetailDirections(impl: DetailDirections): DetailContract.Directions

    @Singleton
    @Binds
    fun bindAddEditDirections(impl: AddEditDirections): AddEditContract.Directions

    @Singleton
    @Binds
    fun bindProfileDirections(impl: ProfileDirections): ProfileContract.Directions
}
