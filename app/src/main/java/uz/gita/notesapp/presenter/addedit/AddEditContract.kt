package uz.gita.notesapp.presenter.addedit

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteType

interface AddEditContract {
    interface ViewModel : OrbitContainerHost<UiAddEditState, UiAddEditState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Init(val id: String?) : Intent
        data class ChangeTitle(val title: String) : Intent
        data class ChangeDescription(val description: String) : Intent
        data class ChangeType(val type: NoteType) : Intent
        data object ToggleFavourite : Intent
        data class AddImages(val uris: List<String>) : Intent
        data class RemoveNewImage(val uri: String) : Intent
        data class RemoveImage(val image: NoteImage) : Intent
        data object Save : Intent
        data class Back(val discard: Boolean = false) : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
        data object ConfirmDiscard : SideEffect
    }

    data class UiAddEditState(
        val initialized: Boolean = false,
        val loading: Boolean = false,
        val saving: Boolean = false,
        val noteId: String? = null,
        val title: String = "",
        val description: String = "",
        val type: NoteType = NoteType.PERSONAL,
        val favourite: Boolean = false,
        val images: List<NoteImage> = emptyList(),
        val newImages: List<String> = emptyList(),
        val titleError: String = "",
        val dirty: Boolean = false
    ) {
        val imageCount: Int get() = images.size + newImages.size
    }

    interface Directions {
        suspend fun back()
    }

    companion object {
        const val MAX_IMAGES = 10
    }
}
