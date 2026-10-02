package uz.gita.notesapp.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import uz.gita.notesapp.domain.model.NoteType

/** Colors Material 3 has no slot for: secondary text, note types, favourites. */
@Immutable
data class NotesColors(
    val muted: Color,
    val faint: Color,
    val line: Color,
    val surface2: Color,
    val star: Color,
    val study: Color,
    val work: Color,
    val personal: Color,
    val idea: Color,
    val link: Color
) {
    fun typeColor(type: NoteType): Color = when (type) {
        NoteType.STUDY -> study
        NoteType.WORK -> work
        NoteType.PERSONAL -> personal
        NoteType.IDEA -> idea
        NoteType.LINK -> link
    }
}

object LightPalette {
    val Background = Color(0xFFF5F6F3)
    val Surface = Color(0xFFFFFFFF)
    val Surface2 = Color(0xFFECEEE9)
    val Line = Color(0xFFE1E4DD)
    val Text = Color(0xFF16201C)
    val Muted = Color(0xFF66726C)
    val Faint = Color(0xFF9AA49F)
    val Accent = Color(0xFF0F766E)
    val OnAccent = Color(0xFFFFFFFF)
    val AccentSoft = Color(0xFFD7EEEA)
    val Danger = Color(0xFFCC3D55)
    val DangerSoft = Color(0xFFFBE4E8)

    val Colors = NotesColors(
        muted = Muted,
        faint = Faint,
        line = Line,
        surface2 = Surface2,
        star = Color(0xFFDE9B00),
        study = Color(0xFF2C66E0),
        work = Color(0xFF7449DB),
        personal = Color(0xFFCC3D55),
        idea = Color(0xFFB58300),
        link = Color(0xFFB53C82)
    )
}

object DarkPalette {
    val Background = Color(0xFF0E1412)
    val Surface = Color(0xFF172019)
    val Surface2 = Color(0xFF202A26)
    val Line = Color(0xFF29332F)
    val Text = Color(0xFFE9EFEC)
    val Muted = Color(0xFF9DAAA4)
    val Faint = Color(0xFF6E7B75)
    val Accent = Color(0xFF3CC2B2)
    val OnAccent = Color(0xFF03201C)
    val AccentSoft = Color(0xFF15352F)
    val Danger = Color(0xFFFF7A8C)
    val DangerSoft = Color(0xFF3A1C23)

    val Colors = NotesColors(
        muted = Muted,
        faint = Faint,
        line = Line,
        surface2 = Surface2,
        star = Color(0xFFF5C04A),
        study = Color(0xFF85A9FF),
        work = Color(0xFFB79BFF),
        personal = Color(0xFFFF7A8C),
        idea = Color(0xFFF0C656),
        link = Color(0xFFF08BC5)
    )
}

val LocalNotesColors = staticCompositionLocalOf { LightPalette.Colors }
