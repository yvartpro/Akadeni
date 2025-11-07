package bi.vovota.akadeni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import bi.vovota.akadeni.data.local.AppDatabase
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.ui.nav.NavGraph
import bi.vovota.akadeni.ui.theme.AkadeniTheme
import bi.vovota.akadeni.viewmodel.LoanViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //DAO
    val dao = AppDatabase.getDatabase(this).loanDao()
    val repo = LoanRepo(dao)
    val viewModel = LoanViewModel(repo)
    enableEdgeToEdge()
    setContent {
      AkadeniTheme {
        NavGraph(viewModel = viewModel)
      }
    }
  }
}