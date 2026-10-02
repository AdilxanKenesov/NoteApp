package uz.gita.notesapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.ThemeMode

interface GetThemeUseCase {
    operator fun invoke(): Flow<ThemeMode>
}
