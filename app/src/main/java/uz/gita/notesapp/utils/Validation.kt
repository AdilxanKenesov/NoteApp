package uz.gita.notesapp.utils

// Plain regex instead of android.util.Patterns so validation also runs in JVM unit tests.
private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun String.isValidEmail(): Boolean = EMAIL_REGEX.matches(this)
