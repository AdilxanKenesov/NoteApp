package uz.gita.notesapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import uz.gita.notesapp.domain.model.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = LightPalette.Accent,
    onPrimary = LightPalette.OnAccent,
    primaryContainer = LightPalette.AccentSoft,
    onPrimaryContainer = LightPalette.Accent,
    secondary = LightPalette.Accent,
    onSecondary = LightPalette.OnAccent,
    secondaryContainer = LightPalette.AccentSoft,
    onSecondaryContainer = LightPalette.Accent,
    background = LightPalette.Background,
    onBackground = LightPalette.Text,
    surface = LightPalette.Background,
    onSurface = LightPalette.Text,
    surfaceVariant = LightPalette.Surface2,
    onSurfaceVariant = LightPalette.Muted,
    surfaceContainerLowest = LightPalette.Surface,
    surfaceContainerLow = LightPalette.Surface,
    surfaceContainer = LightPalette.Surface,
    surfaceContainerHigh = LightPalette.Surface,
    surfaceContainerHighest = LightPalette.Surface2,
    outline = LightPalette.Line,
    outlineVariant = LightPalette.Line,
    error = LightPalette.Danger,
    onError = LightPalette.OnAccent,
    errorContainer = LightPalette.DangerSoft,
    onErrorContainer = LightPalette.Danger,
    inverseSurface = LightPalette.Text,
    inverseOnSurface = LightPalette.Background,
    inversePrimary = DarkPalette.Accent
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPalette.Accent,
    onPrimary = DarkPalette.OnAccent,
    primaryContainer = DarkPalette.AccentSoft,
    onPrimaryContainer = DarkPalette.Accent,
    secondary = DarkPalette.Accent,
    onSecondary = DarkPalette.OnAccent,
    secondaryContainer = DarkPalette.AccentSoft,
    onSecondaryContainer = DarkPalette.Accent,
    background = DarkPalette.Background,
    onBackground = DarkPalette.Text,
    surface = DarkPalette.Background,
    onSurface = DarkPalette.Text,
    surfaceVariant = DarkPalette.Surface2,
    onSurfaceVariant = DarkPalette.Muted,
    surfaceContainerLowest = DarkPalette.Surface,
    surfaceContainerLow = DarkPalette.Surface,
    surfaceContainer = DarkPalette.Surface,
    surfaceContainerHigh = DarkPalette.Surface,
    surfaceContainerHighest = DarkPalette.Surface2,
    outline = DarkPalette.Line,
    outlineVariant = DarkPalette.Line,
    error = DarkPalette.Danger,
    onError = DarkPalette.OnAccent,
    errorContainer = DarkPalette.DangerSoft,
    onErrorContainer = DarkPalette.Danger,
    inverseSurface = DarkPalette.Text,
    inverseOnSurface = DarkPalette.Background,
    inversePrimary = LightPalette.Accent
)

private val NotesShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun NotesAppTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalNotesColors provides if (darkTheme) DarkPalette.Colors else LightPalette.Colors
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            shapes = NotesShapes,
            content = content
        )
    }
}

object NotesTheme {
    val colors: NotesColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNotesColors.current
}
