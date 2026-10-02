package uz.gita.notesapp.data.repository_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.gita.notesapp.data.source.local.SharedManager
import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedManager: SharedManager
) : SettingsRepository {

    override fun getThemeMode(): Flow<ThemeMode> =
        sharedManager.themeModeFlow().map { ThemeMode.from(it) }

    override fun setThemeMode(mode: ThemeMode) {
        sharedManager.saveThemeMode(mode.name)
    }
}
