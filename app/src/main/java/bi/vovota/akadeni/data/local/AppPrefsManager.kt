package bi.vovota.akadeni.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPrefsManager (private val dataStore: DataStore<Preferences>) {
  companion object {
    private val LOCALE = stringPreferencesKey("locale")
  }
  suspend fun saveLocale(locale: String) {
    dataStore.edit { prefs->
      prefs[LOCALE] = locale
    }
  }
  val locale: Flow<String> = dataStore.data
    .map { prefs-> prefs[LOCALE] ?: "rn" }
}