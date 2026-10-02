package uz.gita.notesapp.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData

interface NotesRepository {
    fun getNotes(): Flow<Result<List<NoteUIData>>>
    fun getNote(id: String): Flow<Result<NoteUIData>>

    fun addNote(
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean
    ): Flow<Result<String>>

    fun updateNote(
        id: String,
        title: String,
        description: String,
        type: NoteType,
        favourite: Boolean
    ): Flow<Result<Unit>>

    fun deleteNote(id: String): Flow<Result<Unit>>
    fun setFavourite(id: String, favourite: Boolean): Flow<Result<Unit>>
}
