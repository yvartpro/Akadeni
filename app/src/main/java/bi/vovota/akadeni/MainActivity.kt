package bi.vovota.akadeni

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.preferencesDataStore
import bi.vovota.akadeni.data.local.AppDatabase
import bi.vovota.akadeni.data.local.AppPrefsManager
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.ui.nav.NavGraph
import bi.vovota.akadeni.ui.theme.AkadeniTheme
import bi.vovota.akadeni.utils.LocaleHelper
import bi.vovota.akadeni.viewmodel.LoanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // DAO and repo
    val dao = AppDatabase.getDatabase(this).loanDao()
    val repo = LoanRepo(dao)
    val appPrefs = AppPrefsManager(dataStore)
    val viewModel = LoanViewModel(repo, appPrefs)

    enableEdgeToEdge()

    setContent {
      val lang by appPrefs.locale.collectAsState(initial = "rn")
      val localizedContext = remember(lang) { LocaleHelper.setLocale(this, lang) }

      CompositionLocalProvider(LocalLocalizedContext provides localizedContext) {
        AkadeniTheme {
          NavGraph(
            viewModel = viewModel,
            onLangChange = { newLang ->
              viewModel.setLocale(newLang)
            }
          )
        }
      }
    }
  }
}