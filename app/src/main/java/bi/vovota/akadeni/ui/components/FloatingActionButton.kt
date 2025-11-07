package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.ui.theme.FontSizes

@Composable
fun FAB(
  onClick: () -> Unit
) {

  FloatingActionButton(
    onClick = onClick,
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    shape = CircleShape,
    elevation = FloatingActionButtonDefaults.elevation(
      defaultElevation = 6.dp,
      pressedElevation = 12.dp,
      focusedElevation = 12.dp
    )
  ) {
    Icon(Icons.Filled.Add, contentDescription = null)
  }
}