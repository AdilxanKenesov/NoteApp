package uz.gita.notesapp.domain.usecase.impl

import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.domain.repository.SettingsRepository
import uz.gita.notesapp.domain.usecase.SetThemeUseCase
import javax.inject.Inject

class SetThemeUseCaseImpl @Inject constructor(
    private val repository: SettingsRepository
) : SetThemeUseCase {

    override fun invoke(mode: ThemeMode) {
        repository.setThemeMode(mode)
    }
}
