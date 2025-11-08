package bi.vovota.akadeni.ui.screen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.data.local.model.LoanStatus
import bi.vovota.akadeni.utils.formatDate
import bi.vovota.akadeni.R
import bi.vovota.akadeni.utils.localizedString

@Composable
fun LoanCard(
  loan: Loan,
  onClick: () -> Unit,
  onDelete: () -> Unit,
  context: Context
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 8.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(
      modifier = Modifier
        .padding(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = loan.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = if (loan.status == LoanStatus.PAID)
              loan.status.name.lowercase().replaceFirstChar { it.uppercase() }
            else
              localizedString(R.string.remain, loan.amount - loan.paid),
          style = MaterialTheme.typography.bodyMedium,
          color = when(loan.status) {
            LoanStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
            LoanStatus.PARTIAL -> MaterialTheme.colorScheme.primary
            LoanStatus.PAID -> MaterialTheme.colorScheme.tertiary
          }
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = localizedString(R.string.amount_fb, loan.amount),
          style = MaterialTheme.typography.bodyMedium
        )
        Text(
          text = localizedString(R.string.paid, loan.paid),
          style = MaterialTheme.typography.bodyMedium
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = formatDate(context, loan.createdAt),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error
          )
        }
      }
    }
  }
}