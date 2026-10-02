package uz.gita.notesapp.utils

import android.text.format.DateUtils
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
    val time = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(this))
    if (DateUtils.isToday(this)) return "Today $time"
    if (DateUtils.isToday(this + DateUtils.DAY_IN_MILLIS)) return "Yesterday $time"

    val year = Calendar.getInstance().apply { timeInMillis = this@toCardDate }.get(Calendar.YEAR)
    val thisYear = Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.YEAR)
    val pattern = if (year == thisYear) "d MMM, HH:mm" else "d MMM yyyy"
    return SimpleDateFormat(pattern, Locale.ENGLISH).format(Date(this))
}
