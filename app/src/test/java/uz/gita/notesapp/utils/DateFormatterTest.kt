package uz.gita.notesapp.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateFormatterTest {

    private fun at(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        Calendar.getInstance().apply {
            clear()
            set(year, month, day, hour, minute)
        }.timeInMillis

    private val now = at(2026, Calendar.OCTOBER, 3, 15, 0)

    @Test
    fun `card date picks today, yesterday, this year or full date`() {
        assertEquals("Today 09:05", at(2026, Calendar.OCTOBER, 3, 9, 5).toCardDate(now))
        assertEquals("Yesterday 23:59", at(2026, Calendar.OCTOBER, 2, 23, 59).toCardDate(now))
        assertEquals("1 Jan, 00:00", at(2026, Calendar.JANUARY, 1, 0, 0).toCardDate(now))
        assertEquals("31 Dec 2025", at(2025, Calendar.DECEMBER, 31, 12, 0).toCardDate(now))
    }

    @Test
    fun `yesterday works across a year boundary`() {
        val newYear = at(2027, Calendar.JANUARY, 1, 10, 0)
        assertEquals("Yesterday 20:00", at(2026, Calendar.DECEMBER, 31, 20, 0).toCardDate(newYear))
    }
}
