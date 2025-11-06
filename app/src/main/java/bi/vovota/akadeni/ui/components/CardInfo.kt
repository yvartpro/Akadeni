package bi.vovota.akadeni.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.ui.theme.FontSizes
import bi.vovota.akadeni.ui.theme.Spacings

@Composable
fun InfoCard(
  modifier: Modifier = Modifier,
  //containerColor: Color = MaterialTheme.colorScheme.surface,
  elevation: Dp = 1.dp,
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = Spacings.xs()),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = elevation)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(0.dp),
      content = content
    )
  }
}


@Composable
fun InfoRow(
  label: String,
  value: String,
  labelColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
  valueColor: Color = MaterialTheme.colorScheme.onBackground,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 1.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      color = labelColor,
      fontSize = FontSizes.body()
    )
    Text(
      text = value,
      fontSize = FontSizes.body(),
      style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
      color = valueColor
    )
  }
}