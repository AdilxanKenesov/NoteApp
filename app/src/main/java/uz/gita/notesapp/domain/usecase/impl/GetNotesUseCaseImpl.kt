package uz.gita.notesapp.domain.usecase.impl

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.domain.repository.NotesRepository
import uz.gita.notesapp.domain.usecase.GetNotesUseCase
import javax.inject.Inject

class GetNotesUseCaseImpl @Inject constructor(
    private val repository: NotesRepository
) : GetNotesUseCase {

    override fun invoke(): Flow<Result<List<NoteUIData>>> =
        repository.getNotes()
}
