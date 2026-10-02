package uz.gita.notesapp.presenter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.test
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.usecase.AddNotesUseCase
import uz.gita.notesapp.domain.usecase.DeleteImageUseCase
import uz.gita.notesapp.domain.usecase.GetNoteImagesUseCase
import uz.gita.notesapp.domain.usecase.GetNoteUseCase
import uz.gita.notesapp.domain.usecase.UpdateNotesUseCase
import uz.gita.notesapp.presenter.addedit.AddEditContract.Intent
import uz.gita.notesapp.presenter.addedit.AddEditContract.SideEffect
import uz.gita.notesapp.presenter.addedit.AddEditViewModel

class AddEditViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()

    private val added = mutableListOf<String>()
    private val directions = FakeAddEditDirections()

    private fun viewModel() = AddEditViewModel(
        directions = directions,
        getNoteUseCase = object : GetNoteUseCase {
            override fun invoke(id: String): Flow<Result<NoteUIData>> = emptyFlow()
        },
        getNoteImagesUseCase = object : GetNoteImagesUseCase {
            override fun invoke(noteId: String): Flow<List<NoteImage>> = emptyFlow()
        },
        addNotesUseCase = object : AddNotesUseCase {
            override fun invoke(
                title: String, description: String, type: NoteType, favourite: Boolean,
                imageUris: List<String>
            ): Flow<Result<String>> {
                added += title
                return flowOf(Result.success("new-id"))
            }
        },
        updateNotesUseCase = object : UpdateNotesUseCase {
            override fun invoke(
                id: String, title: String, description: String, type: NoteType, favourite: Boolean,
                newImageUris: List<String>
            ): Flow<Result<Unit>> = flowOf(Result.success(Unit))
        },
        deleteImageUseCase = object : DeleteImageUseCase {
            override fun invoke(image: NoteImage): Flow<Result<Unit>> = flowOf(Result.success(Unit))
        }
    )

    @Test
    fun `saving without a title shows an error`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            viewModel.onEventDispatcher(Intent.Save)
            expectState { copy(titleError = "Add a title") }
        }
        assertEquals(0, added.size)
    }

    @Test
    fun `new note is saved trimmed and the screen closes`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            viewModel.onEventDispatcher(Intent.Init(null))
            expectState { copy(initialized = true) }
            viewModel.onEventDispatcher(Intent.ChangeTitle("  Retrofit timeouts "))
            expectState { copy(title = "  Retrofit timeouts ", dirty = true) }
            viewModel.onEventDispatcher(Intent.Save)
            expectState { copy(saving = true) }
            expectState { copy(saving = false, dirty = false) }
        }
        assertEquals(listOf("Retrofit timeouts"), added)
        assertEquals(1, directions.backCount)
    }

    @Test
    fun `leaving with changes asks for confirmation`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            viewModel.onEventDispatcher(Intent.ChangeTitle("Draft"))
            expectState { copy(title = "Draft", dirty = true) }
            viewModel.onEventDispatcher(Intent.Back())
            expectSideEffect(SideEffect.ConfirmDiscard)
        }
        assertEquals(0, directions.backCount)
    }

    @Test
    fun `image limit is ten per note`() = runTest {
        val viewModel = viewModel()
        viewModel.test(this) {
            viewModel.onEventDispatcher(Intent.AddImages((1..12).map { "content://image/$it" }))
            expectSideEffect(SideEffect.ShowMessage("Only 10 more images fit"))
            expectState { copy(newImages = (1..10).map { "content://image/$it" }, dirty = true) }
        }
    }
}
