package bi.vovota.akadeni.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.ui.components.InputField
import bi.vovota.akadeni.ui.components.MyButton
import bi.vovota.akadeni.ui.components.SmallText
import bi.vovota.akadeni.ui.theme.FontSizes
import bi.vovota.akadeni.viewmodel.LoanViewModel
import bi.vovota.akadeni.R
import bi.vovota.akadeni.utils.localizedString

@Composable
fun AlertDialogModal(
  loan: Loan,
  viewModel: LoanViewModel,
  onDismiss: () -> Unit,
) {
  val paidAmount by viewModel.newAmount.collectAsState()
  val error by viewModel.error.collectAsState()
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(localizedString(R.string.update_pt), fontWeight = FontWeight.Bold, fontSize = FontSizes.body())},
    text = {
      Column {
        if (error.isNotBlank()) {
          SmallText(error, color = MaterialTheme.colorScheme.error)
        }
        Text(localizedString(R.string.enter_paid, loan.name))
        Spacer(Modifier.height(8.dp))
        InputField(
          value = paidAmount,
          onValueChange = { viewModel.setNewAmount(it) },
          label = localizedString(R.string.amount),
          keyboardType = KeyboardType.Decimal,
          imeAction = ImeAction.Done,
          onImeAction = { viewModel.updateLoan(loan)},
          leading = painterResource(R.drawable.money)
        )
      }
    },
    confirmButton = {
      MyButton(text = localizedString(R.string.update)) {
        viewModel.updateLoan(loan)
        onDismiss
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss ) { Text(localizedString(R.string.cancel))}
    }
  )
}