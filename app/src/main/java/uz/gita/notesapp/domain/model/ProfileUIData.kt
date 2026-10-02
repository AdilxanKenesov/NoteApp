package uz.gita.notesapp.domain.model

data class ProfileUIData(
    val name: String,
    val email: String,
    val userImg: String?,
    val isGoogleAccount: Boolean
)
