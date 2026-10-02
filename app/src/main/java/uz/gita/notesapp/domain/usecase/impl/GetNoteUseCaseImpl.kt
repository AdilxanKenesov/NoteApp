package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.usecase.GetNoteUseCase
import javax.inject.Inject

class GetNoteUseCaseImpl @Inject constructor(
    private val repository: NotesRepository
) : GetNoteUseCase {

    override fun invoke(id: String): Flow<Result<NoteUIData>> =
        repository.getNote(id)
}
