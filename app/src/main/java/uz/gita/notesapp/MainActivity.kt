package uz.gita.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.domain.usecase.GetThemeUseCase
import uz.gita.notesapp.navigation.AppNavigationHandler
import uz.gita.notesapp.presenter.splash.SplashScreen
import uz.gita.notesapp.presenter.splash.SplashViewModel
import uz.gita.notesapp.ui.theme.NotesAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    @Inject lateinit var navigationHandler: AppNavigationHandler
    @Inject lateinit var getThemeUseCase: GetThemeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            !splashViewModel.container.stateFlow.value.ready
        }
        // Orbit runs onCreate only once the state is collected; reading .value is not enough.
        lifecycleScope.launch {
            splashViewModel.container.stateFlow.first { it.ready }
        }

        enableEdgeToEdge()
        setContent {
            val themeMode by getThemeUseCase().collectAsStateWithLifecycle(ThemeMode.SYSTEM)

            NotesAppTheme(themeMode = themeMode) {
                Navigator(screen = SplashScreen()) { navigator ->
                    LaunchedEffect(navigator) {
                        navigationHandler.backStack.collectLatest { param ->
                            param.invoke(navigator)
                        }
                    }
                    SlideTransition(navigator)
                }
            }
        }
    }
}
