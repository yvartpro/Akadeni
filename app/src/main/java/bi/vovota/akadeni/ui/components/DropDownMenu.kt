package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.ui.theme.FontSizes
import bi.vovota.akadeni.ui.theme.Spacings

@Composable
fun DropDownMenu(
  menuItems: List<Pair< String, String>>,
  onItemClick: (String) -> Unit,
  modifier: Modifier = Modifier,
  icon: Int
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier.wrapContentSize(Alignment.Center)) {
    IconButton(onClick = { expanded = true }) {
      Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(24.dp)
      )
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      containerColor = MaterialTheme.colorScheme.background,
    ) {
      menuItems.forEach { (code, displayName) ->
        DropdownMenuItem(
          text = { Text(text = displayName, color = MaterialTheme.colorScheme.primary, fontSize = FontSizes.caption()) },
          onClick = { onItemClick(code); expanded = false },
          contentPadding = PaddingValues(horizontal = Spacings.xs()),
          modifier = Modifier.height(28.dp)
        )
      }
    }
  }
}