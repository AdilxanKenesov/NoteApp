package uz.gita.notesapp.navigation

import cafe.adriel.voyager.core.screen.Screen

interface AppNavigator {
    suspend fun navigateTo(screen: Screen)
    suspend fun replaceTo(screen: Screen)
    suspend fun replaceAll(screen: Screen)
    suspend fun back()
}
