package uz.gita.notesapp.presenter.addedit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.viewmodel.orbitContainer
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.usecase.AddNotesUseCase
import uz.gita.notesapp.domain.usecase.DeleteImageUseCase
import uz.gita.notesapp.domain.usecase.GetNoteImagesUseCase
import uz.gita.notesapp.domain.usecase.GetNoteUseCase
import uz.gita.notesapp.domain.usecase.UpdateNotesUseCase
import uz.gita.notesapp.presenter.addedit.AddEditContract.Companion.MAX_IMAGES
import uz.gita.notesapp.presenter.addedit.AddEditContract.Intent
import uz.gita.notesapp.presenter.addedit.AddEditContract.SideEffect
import uz.gita.notesapp.presenter.addedit.AddEditContract.UiAddEditState
import uz.gita.notesapp.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val directions: AddEditContract.Directions,
    private val getNoteUseCase: GetNoteUseCase,
    private val getNoteImagesUseCase: GetNoteImagesUseCase,
    private val addNotesUseCase: AddNotesUseCase,
    private val updateNotesUseCase: UpdateNotesUseCase,
    private val deleteImageUseCase: DeleteImageUseCase
) : ViewModel(), AddEditContract.ViewModel {

    override val container: OrbitContainer<UiAddEditState, UiAddEditState, SideEffect> =
        orbitContainer(UiAddEditState())

    override fun onEventDispatcher(intent: Intent) {
        when (intent) {
            is Intent.Init -> init(intent.id)
            is Intent.ChangeTitle -> edit { copy(title = intent.title, titleError = "") }
            is Intent.ChangeDescription -> edit { copy(description = intent.description) }
            is Intent.ChangeType -> edit { copy(type = intent.type) }
            Intent.ToggleFavourite -> edit { copy(favourite = !favourite) }
            is Intent.AddImages -> addImages(intent.uris)
            is Intent.RemoveNewImage -> edit { copy(newImages = newImages - intent.uri) }
            is Intent.RemoveImage -> removeImage(intent.image)
            Intent.Save -> save()
            is Intent.Back -> back(intent.discard)
        }
    }

    private fun edit(change: UiAddEditState.() -> UiAddEditState) = intent {
        reduce { state.change().copy(dirty = true) }
    }

    // The screen sends Init on every composition; only the first one loads, so a
    // rotation never overwrites what the user has typed.
    private fun init(id: String?) = intent {
        if (state.initialized) return@intent
        reduce { state.copy(initialized = true, noteId = id, loading = id != null) }
        if (id == null) return@intent

        getNoteUseCase(id).first()
            .onSuccess { note ->
                reduce {
                    state.copy(
                        loading = false,
                        title = note.title,
                        description = note.description,
                        type = note.type,
                        favourite = note.favourite
                    )
                }
            }.onFailure { throwable ->
                reduce { state.copy(loading = false) }
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }

        repeatOnSubscription {
            getNoteImagesUseCase(id).collect { images -> reduce { state.copy(images = images) } }
        }
    }

    private fun addImages(uris: List<String>) = intent {
        val free = MAX_IMAGES - state.imageCount
        if (free <= 0) {
            postSideEffect(SideEffect.ShowMessage("Up to $MAX_IMAGES images per note"))
            return@intent
        }
        if (uris.size > free) postSideEffect(SideEffect.ShowMessage("Only $free more images fit"))
        reduce { state.copy(newImages = state.newImages + uris.take(free), dirty = true) }
    }

    private fun removeImage(image: NoteImage) = intent {
        deleteImageUseCase(image).collect { result ->
            result.onFailure { postSideEffect(SideEffect.ShowMessage(it.userMessage())) }
        }
    }

    private fun save() = intent {
        if (state.saving || state.loading) return@intent
        val title = state.title.trim()
        val description = state.description.trimEnd()

        if (title.isEmpty()) {
            reduce { state.copy(titleError = "Add a title") }
            return@intent
        }

        val noteId = state.noteId
        val source: Flow<Result<*>> = if (noteId == null) {
            addNotesUseCase(title, description, state.type, state.favourite, state.newImages)
        } else {
            updateNotesUseCase(noteId, title, description, state.type, state.favourite, state.newImages)
        }

        reduce { state.copy(saving = true) }
        source.collect { result ->
            result.onSuccess {
                reduce { state.copy(saving = false, dirty = false) }
                directions.back()
            }.onFailure { throwable ->
                reduce { state.copy(saving = false) }
                postSideEffect(SideEffect.ShowMessage(throwable.userMessage()))
            }
        }
    }

    private fun back(discard: Boolean) = intent {
        if (state.dirty && !discard) {
            postSideEffect(SideEffect.ConfirmDiscard)
        } else {
            directions.back()
        }
    }
}
