package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.domain.repository.SettingsRepository
import uz.gita.notesapp.domain.usecase.GetThemeUseCase
import javax.inject.Inject

class GetThemeUseCaseImpl @Inject constructor(
    private val repository: SettingsRepository
) : GetThemeUseCase {

    override fun invoke(): Flow<ThemeMode> =
        repository.getThemeMode()
}
