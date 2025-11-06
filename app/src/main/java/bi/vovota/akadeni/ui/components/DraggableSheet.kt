package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.ui.theme.FontSizes
import bi.vovota.akadeni.ui.theme.Spacings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraggableSheet(
  title: String,
  onDismiss: () -> Unit,
  content: @Composable ColumnScope.()-> Unit,
) {
  ModalBottomSheet(
      onDismissRequest = { onDismiss() },
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.secondary
  ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .imePadding()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) { Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          fontSize = FontSizes.title()) }
        Spacer(Modifier.height(Spacings.xs()))
        content()
      }
    }
}


