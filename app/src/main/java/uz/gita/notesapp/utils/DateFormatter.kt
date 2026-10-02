package uz.gita.notesapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toReadableDate(): String {
    if (this <= 0L) return "-"
    return SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date(this))
}

/** "Today 14:32", "Yesterday 09:10", "3 Oct, 14:32" or "3 Oct 2025" for older years. */
fun Long.toCardDate(now: Long = System.currentTimeMillis()): String {
    if (this <= 0L) return ""
    val date = Calendar.getInstance().apply { timeInMillis = this@toCardDate }
    val today = Calendar.getInstance().apply { timeInMillis = now }
    val yesterday = Calendar.getInstance().apply {
        timeInMillis = now
        add(Calendar.DAY_OF_YEAR, -1)
    }
    val time = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(this))

    return when {
        date.isSameDay(today) -> "Today $time"
        date.isSameDay(yesterday) -> "Yesterday $time"
        date.get(Calendar.YEAR) == today.get(Calendar.YEAR) ->
            SimpleDateFormat("d MMM, HH:mm", Locale.ENGLISH).format(Date(this))
        else -> SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date(this))
    }
}

private fun Calendar.isSameDay(other: Calendar): Boolean =
    get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
