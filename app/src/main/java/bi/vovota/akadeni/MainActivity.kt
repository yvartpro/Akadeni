package bi.vovota.akadeni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavGraph(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
          )
        }
      }
    }
  }
}