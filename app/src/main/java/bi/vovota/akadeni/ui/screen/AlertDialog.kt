package bi.vovota.akadeni.ui.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.LoanAction
import bi.vovota.akadeni.R
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.ui.components.InputField
import bi.vovota.akadeni.ui.components.MyButton
import bi.vovota.akadeni.ui.components.SmallText
import bi.vovota.akadeni.LoanViewModel
import bi.vovota.akadeni.utils.localizedString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AlertDialogModal(
  loan: Loan,
  viewModel: LoanViewModel,
  onDismiss: () -> Unit,
) {
  val amount by viewModel.inputAmount.collectAsState()
  val error by viewModel.error.collectAsState()
  val action by viewModel.updateAction.collectAsState()
  val dueDate by viewModel.dueDate.collectAsState()

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

        Spacer(Modifier.height(12.dp))

        // Due-date picker row
        DueDatePickerRow(
          dueDate = dueDate,
          onDateSelected = { viewModel.setDueDate(it) },
          onClear = { viewModel.setDueDate(null) }
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

/**
 * A tappable row that opens a [DatePickerDialog] and shows the selected due date.
 * Displays a "Clear date" button when a date is already set.
 *
 * Uses the native [DatePickerDialog] (no extra library needed) to avoid
 * dependency bloat. The dialog enforces that only future dates can be chosen.
 */
@Composable
fun DueDatePickerRow(
  dueDate: Long?,
  onDateSelected: (Long) -> Unit,
  onClear: () -> Unit,
) {
  val context = LocalContext.current
  val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())

  val calendar = Calendar.getInstance()

  fun openPicker() {
    // Pre-select the existing dueDate in the picker if one is set.
    val preselected = if (dueDate != null) {
      Calendar.getInstance().apply { timeInMillis = dueDate }
    } else calendar

    val dialog = DatePickerDialog(
      context,
      { _: DatePicker, year: Int, month: Int, day: Int ->
        val selected = Calendar.getInstance().apply {
          set(year, month, day, 8, 0, 0) // 08:00 on the chosen day
          set(Calendar.MILLISECOND, 0)
        }
        onDateSelected(selected.timeInMillis)
      },
      preselected.get(Calendar.YEAR),
      preselected.get(Calendar.MONTH),
      preselected.get(Calendar.DAY_OF_MONTH)
    )
    // Only allow future dates
    dialog.datePicker.minDate = System.currentTimeMillis()
    dialog.show()
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
      )
      .clickable { openPicker() }
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(R.drawable.ic_calendar),
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.primary
      )
      Spacer(Modifier.padding(horizontal = 6.dp))
      Text(
        text = if (dueDate != null)
          localizedString(R.string.due_date_set, dateFormat.format(Date(dueDate)))
        else
          localizedString(R.string.set_due_date),
        style = MaterialTheme.typography.bodyMedium,
        color = if (dueDate != null)
          MaterialTheme.colorScheme.primary
        else
          MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
      )
    }
    if (dueDate != null) {
      Text(
        text = localizedString(R.string.clear_date),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.clickable(onClick = onClear)
      )
    }
  }
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
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.01f),
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
