package bi.vovota.akadeni.ui.nav

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.LoanViewModel
import bi.vovota.akadeni.R
import bi.vovota.akadeni.ui.components.DropDownMenu
import bi.vovota.akadeni.ui.components.FAB
import bi.vovota.akadeni.ui.components.SmallText
import bi.vovota.akadeni.ui.screen.HomeScreen
import bi.vovota.akadeni.ui.theme.Spacings
import bi.vovota.akadeni.utils.localizedString
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
  viewModel: LoanViewModel,
  onLangChange: (String) -> Unit
) {
  val context = LocalContext.current
  var lastPressTime by remember { mutableLongStateOf(0L) }
  val searchMode by viewModel.searchMode.collectAsState()
  val query by viewModel.query.collectAsState()
  val loans by viewModel.loans.collectAsState()
  val total = loans.sumOf { it.amount - it.paid }

  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val languages = listOf(
    "rn" to "Kirundi",
    "en" to "English",
    "fr" to "Français"
  )

  BackHandler {
    val currTime = System.currentTimeMillis()
    if (currTime - lastPressTime < 2000) {
      (context as Activity).finish()
    } else {
      lastPressTime = currTime
      Toast.makeText(context, "Touch again to exit", Toast.LENGTH_SHORT).show()
    }
  }
  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet() {
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(Spacings.lg())
          ) {
            Column{
              Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.titleMedium
              )
              Text(
                text = localizedString(R.string.unpaid, total),
                fontWeight = FontWeight.W400,
                style = MaterialTheme.typography.bodySmall
              )
            }
            Column {
              Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.money), "all")
                TextButton(onClick = { viewModel.setAllLoans() }) {
                  Text(localizedString(R.string.all))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.money), "list")
                TextButton(onClick = { viewModel.setPaid() }) {
                  Text(localizedString(R.string.paid_txt))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.history), "list")
                TextButton(onClick = { viewModel.setHistory() }) {
                  Text(localizedString(R.string.not_paid))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, "pending")
                TextButton(onClick = { viewModel.setPartPaid() }) {
                  Text(localizedString(R.string.part_paid))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.recently_deleted), "list")
                TextButton(onClick = { viewModel.setDeleted() }) {
                  Text(localizedString(R.string.recently_deleted))
                }
              }
              Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.lang), "list")
                DropDownMenu(
                  menuItems = languages,
                  label = localizedString(R.string.lang),
                  onItemClick = { selectedCode-> onLangChange(selectedCode) }
                )
              }
            }
          }
          Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(painterResource(R.drawable.vovota), "list",modifier = Modifier.size(24.dp))
            SmallText(localizedString(R.string.powered))
          }
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            if (searchMode)
              BasicTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it)},
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                  color = MaterialTheme.colorScheme.onSurface
                ),
                decorationBox = { innerTextField->
                  if (query.isEmpty())
                    Text(
                      text = "Search...",
                      style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                      )
                    )
                  else
                    innerTextField()
                }
              )
            else
              Column{
                Text(
                  text = stringResource(R.string.app_name),
                  fontWeight = FontWeight.W700,
                  style = MaterialTheme.typography.titleMedium
                )
                Text(
                  text = localizedString(R.string.unpaid, total),
                  fontWeight = FontWeight.W400,
                  style = MaterialTheme.typography.bodySmall
                )
              }
          },
          actions = {
            IconButton(onClick = { viewModel.toggleSearchMode() }) {
              Icon(
                imageVector = if (searchMode) Icons.Outlined.Clear else Icons.Outlined.Search,
                contentDescription = "notification",
                modifier = Modifier.size(24.dp)
              )
            }
            IconButton(onClick = {
              scope.launch {
                if ( drawerState.isClosed) drawerState.open() else drawerState.open()
              }
            }) {
              Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "menu",
                modifier = Modifier.size(24.dp)
              )
            }
          }
        )
      },
      floatingActionButton = {
        FAB(onClick = { viewModel.toggleShowCreateSheet() })
      },
      content = { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .background(MaterialTheme.colorScheme.surfaceContainer)
      ) {
        HomeScreen(viewModel)
      }
    }
    )
  }
}