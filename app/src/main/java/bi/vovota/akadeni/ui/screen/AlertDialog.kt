package bi.vovota.akadeni.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.LoanAction
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.ui.components.InputField
import bi.vovota.akadeni.ui.components.MyButton
import bi.vovota.akadeni.ui.components.SmallText
import bi.vovota.akadeni.LoanViewModel
import bi.vovota.akadeni.R
import bi.vovota.akadeni.utils.localizedString

@Composable
fun AlertDialogModal(
  loan: Loan,
  viewModel: LoanViewModel,
  onDismiss: () -> Unit,
) {
  val amount by viewModel.inputAmount.collectAsState()
  val error by viewModel.error.collectAsState()

  val action by viewModel.updateAction.collectAsState()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = localizedString(R.string.update_pt),
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column {

        if (error.isNotBlank()) {
          SmallText(error, color = MaterialTheme.colorScheme.error)
        }

        // Action chooser (two tabs)
        ActionSelector(
          viewModel = viewModel,
          selected = action,
        )

        Spacer(Modifier.height(8.dp))

        // Dynamic label text
        Text(
          text = when (action) {
            LoanAction.PAY ->
              localizedString(R.string.enter_paid, loan.name)
            LoanAction.INCREASE ->
              localizedString(R.string.enter_increase, loan.name)
          }
        )

        Spacer(Modifier.height(6.dp))

        // Shared input
        InputField(
          value = amount,
          onValueChange = { viewModel.setInputAmount(it) },
          label = when (action) {
            LoanAction.PAY -> localizedString(R.string.amount_paid)
            LoanAction.INCREASE -> localizedString(R.string.amount_to_add)
          },
          keyboardType = KeyboardType.Decimal,
          imeAction = ImeAction.Done,
          onImeAction = {
            viewModel.updateLoan(loan)
          },
          leading = painterResource(id = R.drawable.money)
        )
      }
    },
    confirmButton = {
      MyButton(
        text = when (action) {
          LoanAction.PAY -> localizedString(R.string.mark_as_paid)
          LoanAction.INCREASE -> localizedString(R.string.add_to_loan)
        }
      ) {
        viewModel.updateLoan(loan)
      }
    },
    dismissButton = {
      TextButton(onClick = {
        onDismiss()
        viewModel.setActionPay()
      }) {
        Text(localizedString(R.string.cancel))
      }
    }
  )
}

@Composable
fun ActionSelector(
  viewModel: LoanViewModel,
  selected: LoanAction,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = MaterialTheme.shapes.large
      )
      .padding(4.dp)
  ) {

    ActionChip(
      text = localizedString(R.string.pay),
      selected = selected == LoanAction.PAY,
      onClick = { viewModel.setActionPay() },
      modifier = Modifier.weight(1f)
    )

    ActionChip(
      text = localizedString(R.string.add),
      selected = selected == LoanAction.INCREASE,
      onClick = { viewModel.setActionAdd() },
      modifier = Modifier.weight(1f),
      isLeft = false
    )
  }
}

@Composable
fun ActionChip(
  modifier: Modifier = Modifier,
  text: String,
  selected: Boolean,
  isLeft: Boolean = true,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(42.dp)
      .background(
        color = if (selected)
          MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surface,
        shape = if (isLeft)
          RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp, topEnd = 0.dp, bottomEnd = 0.dp)
        else
          RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp)
      )
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      color = if (selected)
        MaterialTheme.colorScheme.primary
      else MaterialTheme.colorScheme.onSurface
    )
  }
}
