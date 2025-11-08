package bi.vovota.akadeni.ui.nav

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.viewmodel.LoanViewModel
import bi.vovota.akadeni.R
import bi.vovota.akadeni.ui.components.DropDownMenu
import bi.vovota.akadeni.ui.components.FAB
import bi.vovota.akadeni.ui.screen.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
  viewModel: LoanViewModel,
  lang: String,
  onLangChange: (String) -> Unit
) {
  val context = LocalContext.current
  var lastPressTime by remember { mutableLongStateOf(0L) }
  val searchMode by viewModel.searchMode.collectAsState()
  val query by viewModel.query.collectAsState()
  val languages = listOf(
    "rn" to "Kirundi",
    "en" to "English",
    "fr" to "Francais"
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
            Text(
              text = stringResource(R.string.app_name),
              fontWeight = FontWeight.W700,
              style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
          IconButton(onClick = { viewModel.toggleSearchMode() }) {
            Icon(
              imageVector = if (searchMode) Icons.Outlined.Clear else Icons.Outlined.Search,
              contentDescription = "notification",
              modifier = Modifier.size(24.dp)
            )
          }
          DropDownMenu(
            menuItems = languages,
            icon = R.drawable.lang,
            onItemClick = { selectedCode-> onLangChange(selectedCode) }
          )
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