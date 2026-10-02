package uz.gita.notesapp.domain.model

enum class NoteType(val title: String) {
    STUDY("Study"),
    WORK("Work"),
    PERSONAL("Personal"),
    IDEA("Idea"),
    LINK("Link");

    companion object {
        // Notes saved before the categories changed still carry the old names.
        private val legacy = mapOf(
            "LEARNING" to STUDY,
            "CODE_SNIPPET" to WORK,
            "BUG_FIX" to PERSONAL,
            "USEFUL_LINK" to LINK
        )

        fun from(value: String): NoteType =
            entries.firstOrNull { it.name == value } ?: legacy[value] ?: PERSONAL
    }
}
