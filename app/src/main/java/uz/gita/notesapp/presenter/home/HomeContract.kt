package uz.gita.notesapp.presenter.home

import org.orbitmvi.orbit.OrbitContainerHost
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData

interface HomeContract {
    interface ViewModel : OrbitContainerHost<UiHomeState, UiHomeState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface Intent {
        data object OpenProfile : Intent
        data object OpenAddNote : Intent
        data class Search(val query: String) : Intent
        data class SelectFilter(val filter: HomeFilter) : Intent
        data class ToggleFavourite(val id: String, val favourite: Boolean) : Intent
        data class OpenDetail(val id: String) : Intent
        data class Delete(val id: String) : Intent
        data class UndoDelete(val id: String) : Intent
    }

    sealed interface SideEffect {
        data class ShowMessage(val message: String) : SideEffect
        data class ShowUndo(val id: String) : SideEffect
    }

    sealed interface HomeFilter {
        data object All : HomeFilter
        data object Favourites : HomeFilter
        data class Type(val type: NoteType) : HomeFilter
    }

    data class UiHomeState(
        val loading: Boolean = true,
        val query: String = "",
        val filter: HomeFilter = HomeFilter.All,
        val userName: String = "",
        val userImg: String? = null,
        val allNotes: List<NoteUIData> = emptyList(),
        val notes: List<NoteUIData> = emptyList(),
        val pendingDelete: Set<String> = emptySet(),
        val firstImages: Map<String, NoteImage> = emptyMap()
    )

    interface Directions {
        suspend fun navigateToProfile()
        suspend fun navigateToAddNote()
        suspend fun navigateToDetail(id: String)
    }
}
