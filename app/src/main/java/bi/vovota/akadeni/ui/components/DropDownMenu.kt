package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bi.vovota.akadeni.ui.theme.FontSizes

@Composable
fun DropDownMenu(
  menuItems: List<Pair<String, String>>,
  onItemClick: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String? = null,
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier.wrapContentSize(Alignment.Center)) {
    TextButton(onClick = { expanded = !expanded }) {
      if (!label.isNullOrEmpty()) {
        Text(label)
      }
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      containerColor = MaterialTheme.colorScheme.background,
    ) {
      menuItems.forEach { (code, displayName) ->
        DropdownMenuItem(
          text = {
            Text(
              text = displayName,
              color = MaterialTheme.colorScheme.primary,
              fontSize = FontSizes.caption()
            )
          },
          onClick = {
            onItemClick(code)
            expanded = false
          }
        )
      }
    }
  }
}
