package uz.gita.notesapp.domain.model

data class NoteUIData(
    val id: String,
    val title: String,
    val description: String,
    val type: NoteType,
    val favourite: Boolean,
    val createdAt: Long
)
