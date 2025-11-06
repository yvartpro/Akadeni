package bi.vovota.akadeni.ui.nav

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bi.vovota.akadeni.ui.screen.WelcomeScreen
import bi.vovota.akadeni.viewmodel.LoanViewModel

sealed class Route(val route: String) {
  object Welcome: Route("welcome")
  object Home: Route("home")
  object Loan: Route("loan/{loanId}")
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
  modifier: Modifier = Modifier,
  viewModel: LoanViewModel
) {
  val navController = rememberNavController()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = " Akadeni", fontWeight = FontWeight.W700, style = MaterialTheme.typography.titleLarge) },
        modifier = modifier.fillMaxWidth(),
        actions = {
          IconButton(onClick = {}) {
            Icon(
              imageVector = Icons.Filled.Notifications,
              contentDescription = "notification"
            )
          }
        }
      )
    }
  ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .background(MaterialTheme.colorScheme.background),
      ) {
        NavHost(navController, startDestination = Route.Welcome.route) {
          composable(Route.Welcome.route) {
            WelcomeScreen(viewModel)
          }
        }
      }
  }
}