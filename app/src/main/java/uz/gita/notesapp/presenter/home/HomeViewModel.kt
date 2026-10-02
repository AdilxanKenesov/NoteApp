package uz.gita.notesapp.presenter.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.usecase.DeleteNotesUseCase
import uz.gita.notesapp.domain.usecase.GetAllImagesUseCase
import uz.gita.notesapp.domain.usecase.GetNotesUseCase
import uz.gita.notesapp.domain.usecase.GetProfileUseCase
import uz.gita.notesapp.domain.usecase.SetFavouriteUseCase
import uz.gita.notesapp.presenter.home.HomeContract.HomeFilter
import uz.gita.notesapp.presenter.home.HomeContract.Intent
import uz.gita.notesapp.presenter.home.HomeContract.SideEffect
import uz.gita.notesapp.presenter.home.HomeContract.UiHomeState
import uz.gita.notesapp.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val directions: HomeContract.Directions,
    private val getNotesUseCase: GetNotesUseCase,
    private val getAllImagesUseCase: GetAllImagesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val setFavouriteUseCase: SetFavouriteUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase
) : ViewModel(), HomeContract.ViewModel {

    override val container: OrbitContainer<UiHomeState, UiHomeState, SideEffect> =
        orbitContainer(UiHomeState()) {
            loadProfile()
            observeNotes()
        }

    override fun onEventDispatcher(intent: Intent) {
        when (intent) {
            Intent.OpenProfile -> intent { directions.navigateToProfile() }
            Intent.OpenAddNote -> intent { directions.navigateToAddNote() }
            is Intent.OpenDetail -> intent { directions.navigateToDetail(intent.id) }
            is Intent.Search -> intent {
                reduce { state.copy(query = intent.query).withVisibleNotes() }
            }
            is Intent.SelectFilter -> intent {
                reduce { state.copy(filter = intent.filter).withVisibleNotes() }
            }
            is Intent.ToggleFavourite -> toggleFavourite(intent.id, intent.favourite)
            is Intent.Delete -> delete(intent.id)
            is Intent.UndoDelete -> intent {
                reduce { state.copy(pendingDelete = state.pendingDelete - intent.id).withVisibleNotes() }
            }
        }
    }

    // Firestore listener + Room images, collected only while the screen is visible.
    private fun observeNotes() = intent {
        repeatOnSubscription {
            combine(getNotesUseCase(), getAllImagesUseCase()) { notes, images -> notes to images }
                .collect { (result, images) ->
                    val firstImages = images.groupBy { it.noteId }.mapValues { it.value.first() }
                    result.onSuccess { notes ->
                        reduce {
                            state.copy(loading = false, allNotes = notes, firstImages = firstImages)
                                .withVisibleNotes()
                        }
                    }.onFailure { throwable ->
                        reduce { state.copy(loading = false) }
                        postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
                    }
                }
        }
    }

    private fun loadProfile() = intent {
        getProfileUseCase().collect { result ->
            result.onSuccess { profile ->
                reduce { state.copy(userName = profile.name.ifBlank { profile.email }, userImg = profile.userImg) }
            }
        }
    }

    private fun toggleFavourite(id: String, favourite: Boolean) = intent {
        // Optimistic: the star changes at once, the snapshot listener confirms it.
        reduce {
            state.copy(allNotes = state.allNotes.map { if (it.id == id) it.copy(favourite = favourite) else it })
                .withVisibleNotes()
        }
        setFavouriteUseCase(id, favourite).collect { result ->
            result.onFailure { throwable ->
                reduce {
                    state.copy(allNotes = state.allNotes.map { if (it.id == id) it.copy(favourite = !favourite) else it })
                        .withVisibleNotes()
                }
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }

    // A swiped note is only hidden first; it is deleted (with its images) once the
    // Undo window has passed. The container outlives the snackbar, so leaving the
    // screen does not cancel the delete.
    private fun delete(id: String) = intent {
        if (id in state.pendingDelete) return@intent
        reduce { state.copy(pendingDelete = state.pendingDelete + id).withVisibleNotes() }
        postSideEffect(SideEffect.ShowUndo(id))

        delay(UNDO_WINDOW_MS)
        if (id !in state.pendingDelete) return@intent

        deleteNotesUseCase(id).collect { result ->
            result.onSuccess {
                // Drop it now so it does not flash back before the snapshot listener catches up.
                reduce {
                    state.copy(allNotes = state.allNotes.filterNot { it.id == id }, pendingDelete = state.pendingDelete - id)
                        .withVisibleNotes()
                }
            }.onFailure { throwable ->
                reduce { state.copy(pendingDelete = state.pendingDelete - id).withVisibleNotes() }
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }

    private fun UiHomeState.withVisibleNotes(): UiHomeState {
        val query = query.trim()
        val visible = allNotes
            .filter { it.id !in pendingDelete }
            .filter { it.matches(filter) }
            .filter { note ->
                query.isEmpty() ||
                        note.title.contains(query, ignoreCase = true) ||
                        note.description.contains(query, ignoreCase = true)
            }
        return copy(notes = visible)
    }

    private fun NoteUIData.matches(filter: HomeFilter): Boolean = when (filter) {
        HomeFilter.All -> true
        HomeFilter.Favourites -> favourite
        is HomeFilter.Type -> type == filter.type
    }

    private companion object {
        // Slightly longer than SnackbarDuration.Short, so Undo is always in time.
        const val UNDO_WINDOW_MS = 4_500L
    }
}
