package uz.gita.notesapp.presenter.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import uz.gita.notesapp.ui.theme.NotesAppTheme
import uz.gita.notesapp.ui.theme.LightPalette

// The system splash (Theme.NotesApp.Starting) shows the logo; this only fills
// the frame between the splash and the first real screen with the same color.
class SplashScreen : Screen {

    @Composable
    override fun Content() {
        SplashScreenContent()
    }
}

@Composable
private fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightPalette.Accent)
    )
}

@Preview
@Composable
private fun PreviewSplashScreenContent() {
    NotesAppTheme { SplashScreenContent() }
}
