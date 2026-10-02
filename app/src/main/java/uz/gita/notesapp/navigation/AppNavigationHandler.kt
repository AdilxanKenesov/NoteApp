package uz.gita.notesapp.navigation

import kotlinx.coroutines.flow.Flow

interface AppNavigationHandler {
    val backStack: Flow<AppNavigationParam>
}
