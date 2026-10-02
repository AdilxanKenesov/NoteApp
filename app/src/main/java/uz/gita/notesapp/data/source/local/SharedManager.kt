package uz.gita.notesapp.data.source.local

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedManager @Inject constructor(
    private val prefs: SharedPreferences
) {

    fun saveThemeMode(mode: String) {
        prefs.edit { putString(KEY_THEME, mode) }
    }

    fun getThemeMode(): String? = prefs.getString(KEY_THEME, null)

    fun themeModeFlow(): Flow<String?> = callbackFlow {
        trySend(getThemeMode())

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_THEME) trySend(getThemeMode())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    private companion object {
        const val KEY_THEME = "theme_mode"
    }
}
