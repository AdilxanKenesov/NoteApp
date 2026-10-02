package uz.gita.notesapp.presenter.detail

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteUIData

interface DetailContract {
    interface ViewModel : OrbitContainerHost<UiDetailState, UiDetailState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data class Init(val id: String) : Intent
        data object Edit : Intent
        data object Delete : Intent
        data object ToggleFavourite : Intent
        data object Back : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
    }

    data class UiDetailState(
        val noteId: String? = null,
        val loading: Boolean = true,
        val deleting: Boolean = false,
        val note: NoteUIData? = null,
        val images: List<NoteImage> = emptyList()
    )

    interface Directions {
        suspend fun navigateToEdit(id: String)
        suspend fun back()
    }
}
