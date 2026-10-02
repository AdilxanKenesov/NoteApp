package uz.gita.notesapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.ThemeMode

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>
    fun setThemeMode(mode: ThemeMode)
}
