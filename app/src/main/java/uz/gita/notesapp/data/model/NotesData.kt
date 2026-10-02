package uz.gita.notesapp.data.model

data class NotesData(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "",
    val favourite: Boolean = false,
    val createdAt: Long = 0L
)
