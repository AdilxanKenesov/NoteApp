package uz.gita.notesapp.screenshots

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.model.ProfileUIData
import uz.gita.notesapp.domain.model.ThemeMode
import uz.gita.notesapp.presenter.addedit.AddEditContract
import uz.gita.notesapp.presenter.addedit.AddEditScreenContent
import uz.gita.notesapp.presenter.detail.DetailContract
import uz.gita.notesapp.presenter.detail.DetailScreenContent
import uz.gita.notesapp.presenter.home.HomeContract
import uz.gita.notesapp.presenter.home.HomeScreenContent
import uz.gita.notesapp.presenter.login.LoginContract
import uz.gita.notesapp.presenter.login.LoginScreenContent
import uz.gita.notesapp.presenter.profile.ProfileContract
import uz.gita.notesapp.presenter.profile.ProfileScreenContent
import uz.gita.notesapp.ui.theme.NotesAppTheme

/**
 * Renders the screens with demo data into docs/screenshots for the README.
 * Regenerate with: ./gradlew recordRoborazziDebug
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Plain Application: the screens need no Hilt graph, only their state.
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi", application = Application::class)
class ReadmeScreenshots {

    @get:Rule val composeRule = createComposeRule()

    @Test fun login() = capture("login") {
        LoginScreenContent(
            state = LoginContract.UiLoginState(),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {},
            onGoogleClick = {}
        )
    }

    @Test fun homeLight() = capture("home_light") { Home() }

    @Test fun homeDark() = capture("home_dark", ThemeMode.DARK) { Home() }

    @Test fun detail() = capture("detail") {
        DetailScreenContent(
            state = DetailContract.UiDetailState(noteId = "1", loading = false, note = demoNotes[0]),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {}
        )
    }

    @Test fun addEdit() = capture("add_note", ThemeMode.DARK) {
        AddEditScreenContent(
            state = AddEditContract.UiAddEditState(
                initialized = true,
                title = "Weekend trip",
                description = "Train to Samarkand on Saturday 08:00.\n\nPack: charger, camera, jacket.\nBook the guesthouse near Registan.",
                type = NoteType.PERSONAL,
                favourite = true
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {}
        )
    }

    @Test fun profile() = capture("profile") {
        ProfileScreenContent(
            state = ProfileContract.UiProfileState(
                loading = false,
                profile = ProfileUIData("Alex Morgan", "alex.morgan@example.com", null, isGoogleAccount = false),
                themeMode = ThemeMode.SYSTEM
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {},
            onConfirmWithGoogle = {}
        )
    }

    @Composable
    private fun Home() {
        HomeScreenContent(
            state = HomeContract.UiHomeState(
                loading = false,
                userName = "Alex Morgan",
                allNotes = demoNotes,
                notes = demoNotes
            ),
            snackbarHostState = SnackbarHostState(),
            onEventDispatcher = {}
        )
    }

    private fun capture(name: String, theme: ThemeMode = ThemeMode.LIGHT, content: @Composable () -> Unit) {
        composeRule.setContent { NotesAppTheme(themeMode = theme, content = content) }
        composeRule.onRoot().captureRoboImage("../docs/screenshots/$name.png")
    }

    private companion object {
        val now = System.currentTimeMillis()
        const val HOUR = 3_600_000L

        val demoNotes = listOf(
            NoteUIData(
                id = "1",
                title = "Sprint planning",
                description = "Move the release to Friday.\nAsk design for the new onboarding icons.\nReview the analytics dashboard with the team.",
                type = NoteType.WORK,
                favourite = true,
                createdAt = now - HOUR
            ),
            NoteUIData(
                id = "2",
                title = "Chapter 4 · Market structures",
                description = "Perfect competition vs monopoly, price elasticity and examples",
                type = NoteType.STUDY,
                favourite = false,
                createdAt = now - 5 * HOUR
            ),
            NoteUIData(
                id = "3",
                title = "Habit tracker with streaks",
                description = "Small widget that shows today's streak on the home screen",
                type = NoteType.IDEA,
                favourite = true,
                createdAt = now - 26 * HOUR
            ),
            NoteUIData(
                id = "4",
                title = "Weekend trip",
                description = "Train to Samarkand on Saturday 08:00",
                type = NoteType.PERSONAL,
                favourite = false,
                createdAt = now - 3 * 24 * HOUR
            ),
            NoteUIData(
                id = "5",
                title = "Material 3 color guide",
                description = "m3.material.io/styles/color",
                type = NoteType.LINK,
                favourite = false,
                createdAt = now - 9 * 24 * HOUR
            )
        )
    }
}
