package bi.vovota.akadeni

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import bi.vovota.akadeni.data.local.AppDatabase
import bi.vovota.akadeni.data.local.AppPrefsManager
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.notification.NotificationHelper
import bi.vovota.akadeni.ui.nav.NavGraph
import bi.vovota.akadeni.ui.theme.AkadeniTheme
import bi.vovota.akadeni.utils.LocaleHelper

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class MainActivity : ComponentActivity() {

  /**
   * Runtime permission launcher for POST_NOTIFICATIONS (required on Android 13+).
   * Registered before onCreate per the ActivityResult API contract.
   * We do not block the user if they deny — reminders are silently skipped.
   */
  private val notificationPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { /* granted or denied – no blocking UI either way */ }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Ensure the notification channel exists before any worker can fire.
    NotificationHelper.createChannel(this)

    // Request POST_NOTIFICATIONS on Android 13+ if not already granted.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
          != PackageManager.PERMISSION_GRANTED) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    // Build the ViewModel using AndroidViewModelFactory so it receives Application context.
    val dao = AppDatabase.getDatabase(this).loanDao()
    val repo = LoanRepo(dao)
    val appPrefs = AppPrefsManager(dataStore)
    val viewModel = ViewModelProvider(
      this,
      LoanViewModelFactory(application, repo, appPrefs)
    )[LoanViewModel::class.java]

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