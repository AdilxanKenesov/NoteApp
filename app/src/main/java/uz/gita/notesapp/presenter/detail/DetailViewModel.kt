package uz.gita.notesapp.presenter.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.usecase.DeleteNotesUseCase
import uz.gita.notesapp.domain.usecase.GetNoteImagesUseCase
import uz.gita.notesapp.domain.usecase.GetNoteUseCase
import uz.gita.notesapp.domain.usecase.SetFavouriteUseCase
import uz.gita.notesapp.presenter.detail.DetailContract.Intent
import uz.gita.notesapp.presenter.detail.DetailContract.SideEffect
import uz.gita.notesapp.presenter.detail.DetailContract.UiDetailState
import uz.gita.notesapp.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val directions: DetailContract.Directions,
    private val getNoteUseCase: GetNoteUseCase,
    private val getNoteImagesUseCase: GetNoteImagesUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val setFavouriteUseCase: SetFavouriteUseCase
) : ViewModel(), DetailContract.ViewModel {

    override val container: OrbitContainer<UiDetailState, UiDetailState, SideEffect> =
        orbitContainer(UiDetailState())

    override fun onEventDispatcher(intent: Intent) {
        when (intent) {
            is Intent.Init -> observeNote(intent.id)
            Intent.Edit -> intent { state.noteId?.let { directions.navigateToEdit(it) } }
            Intent.Delete -> deleteNote()
            Intent.ToggleFavourite -> toggleFavourite()
            Intent.Back -> intent { directions.back() }
        }
    }

    private fun observeNote(id: String) = intent {
        // Init is sent from the screen on every composition (rotation, coming back from edit);
        // the note is subscribed to only once.
        if (state.noteId == id) return@intent
        reduce { state.copy(noteId = id) }

        repeatOnSubscription {
            combine(getNoteUseCase(id), getNoteImagesUseCase(id)) { note, images -> note to images }
                .collect { (result, images) ->
                    if (state.deleting) return@collect
                    result.onSuccess { note ->
                        reduce { state.copy(loading = false, note = note, images = images) }
                    }.onFailure { throwable ->
                        postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
                        directions.back()
                    }
                }
        }
    }

    private fun toggleFavourite() = intent {
        val note = state.note ?: return@intent
        setFavouriteUseCase(note.id, !note.favourite).collect { result ->
            result.onFailure { postSideEffect(SideEffect.ShowMessage(it.userMessage())) }
        }
    }

    private fun deleteNote() = intent {
        val id = state.noteId ?: return@intent
        if (state.deleting) return@intent
        reduce { state.copy(deleting = true) }

        deleteNotesUseCase(id).collect { result ->
            result.onSuccess {
                directions.back()
            }.onFailure { throwable ->
                reduce { state.copy(deleting = false) }
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }
}
