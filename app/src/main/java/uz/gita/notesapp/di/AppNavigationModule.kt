package uz.gita.notesapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.notesapp.navigation.AppNavigationDispatcher
import uz.gita.notesapp.navigation.AppNavigationHandler
import uz.gita.notesapp.navigation.AppNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppNavigationModule {

    @Provides
    @Singleton
    fun provideAppNavigator(): AppNavigator = AppNavigationDispatcher

    @Provides
    @Singleton
    fun provideAppNavigationHandler(): AppNavigationHandler = AppNavigationDispatcher
}