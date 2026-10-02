package uz.gita.notesapp.domain.usecase

import uz.gita.notesapp.domain.model.ThemeMode

interface SetThemeUseCase {
    operator fun invoke(mode: ThemeMode)
}
