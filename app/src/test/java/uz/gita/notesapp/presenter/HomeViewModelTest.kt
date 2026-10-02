package uz.gita.notesapp.presenter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.test
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.model.ProfileUIData
import uz.gita.notesapp.domain.usecase.DeleteNotesUseCase
import uz.gita.notesapp.domain.usecase.GetAllImagesUseCase
import uz.gita.notesapp.domain.usecase.GetNotesUseCase
import uz.gita.notesapp.domain.usecase.GetProfileUseCase
import uz.gita.notesapp.domain.usecase.SetFavouriteUseCase
import uz.gita.notesapp.presenter.home.HomeContract.HomeFilter
import uz.gita.notesapp.presenter.home.HomeContract.Intent
import uz.gita.notesapp.presenter.home.HomeContract.SideEffect
import uz.gita.notesapp.presenter.home.HomeContract.UiHomeState
import uz.gita.notesapp.presenter.home.HomeViewModel

class HomeViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private val work = note("1", "Sprint planning", NoteType.WORK, favourite = true)
    private val personal = note("2", "Dentist on Friday", NoteType.PERSONAL)
    private val study = note("3", "Chapter 4 summary", NoteType.STUDY)
    private val deleted = mutableListOf<String>()

    private fun viewModel() = HomeViewModel(
        directions = FakeHomeDirections(),
        getNotesUseCase = object : GetNotesUseCase {
            override fun invoke(): Flow<Result<List<NoteUIData>>> = flowOf(Result.success(listOf(work, personal, study)))
        },
        getAllImagesUseCase = object : GetAllImagesUseCase {
            override fun invoke(): Flow<List<NoteImage>> = flowOf(emptyList())
        },
        getProfileUseCase = object : GetProfileUseCase {
            override fun invoke(): Flow<Result<ProfileUIData>> = flowOf(Result.failure(Exception()))
        },
        setFavouriteUseCase = object : SetFavouriteUseCase {
            override fun invoke(id: String, favourite: Boolean): Flow<Result<Unit>> = flowOf(Result.success(Unit))
        },
        deleteNotesUseCase = object : DeleteNotesUseCase {
            override fun invoke(id: String): Flow<Result<Unit>> {
                deleted += id
                return flowOf(Result.success(Unit))
            }
        }
    )

    @Test
    fun `filters and search narrow the list`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            runOnCreate()
            val loaded = awaitLoaded()
            assertEquals(listOf(work, personal, study), loaded.notes)

            viewModel.onEventDispatcher(Intent.SelectFilter(HomeFilter.Favourites))
            assertEquals(listOf(work), awaitState().notes)

            viewModel.onEventDispatcher(Intent.SelectFilter(HomeFilter.Type(NoteType.STUDY)))
            assertEquals(listOf(study), awaitState().notes)

            viewModel.onEventDispatcher(Intent.SelectFilter(HomeFilter.All))
            awaitState()
            viewModel.onEventDispatcher(Intent.Search("dentist"))
            assertEquals(listOf(personal), awaitState().notes)

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `swiped note hides at once and undo brings it back`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            runOnCreate()
            awaitLoaded()

            viewModel.onEventDispatcher(Intent.Delete(personal.id))
            assertEquals(listOf(work, study), awaitState().notes)
            expectSideEffect(SideEffect.ShowUndo(personal.id))

            viewModel.onEventDispatcher(Intent.UndoDelete(personal.id))
            assertEquals(listOf(work, personal, study), awaitState().notes)

            testScheduler.advanceTimeBy(5_000)
            testScheduler.runCurrent()
            assertEquals(emptyList<String>(), deleted)
            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `swiped note is deleted after the undo window`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            runOnCreate()
            awaitLoaded()

            viewModel.onEventDispatcher(Intent.Delete(personal.id))
            assertEquals(listOf(work, study), awaitState().notes)
            expectSideEffect(SideEffect.ShowUndo(personal.id))

            testScheduler.advanceTimeBy(5_000)
            testScheduler.runCurrent()
            assertEquals(listOf(personal.id), deleted)
            cancelAndIgnoreRemainingItems()
        }
    }

    private suspend fun org.orbitmvi.orbit.test.OrbitTestContext<UiHomeState, *, *>.awaitLoaded(): UiHomeState {
        var state = awaitState()
        while (state.loading) state = awaitState()
        return state
    }

    private fun note(
        id: String,
        title: String,
        type: NoteType,
        favourite: Boolean = false
    ) = NoteUIData(id, title, "", type, favourite, 0L)
}
