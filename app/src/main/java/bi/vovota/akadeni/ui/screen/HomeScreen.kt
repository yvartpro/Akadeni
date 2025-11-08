package bi.vovota.akadeni.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.ui.components.InputField
import bi.vovota.akadeni.ui.components.SmallText
import bi.vovota.akadeni.viewmodel.LoanViewModel
import bi.vovota.akadeni.R
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.ui.components.DraggableSheet
import bi.vovota.akadeni.ui.components.MyButton
import bi.vovota.akadeni.ui.theme.Spacings
import bi.vovota.akadeni.utils.localizedString

@Composable
fun HomeScreen(
  viewModel: LoanViewModel,
) {
  val loans by viewModel.loans.collectAsState()
  val name by viewModel.name.collectAsState()
  val amount by viewModel.amount.collectAsState()
  val isSheetVisible by viewModel.isCreateSheetVisible.collectAsState()
  val showAlert by viewModel.isAlertVisible.collectAsState()
  var selectedLoan by remember { mutableStateOf<Loan?>(null) }
  val query by viewModel.query.collectAsState()
  val error by viewModel.error.collectAsState()

  val filteredLoans = loans.filter { it.name.contains(query,ignoreCase = true) }

  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(Spacings.sm()),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    if (isSheetVisible) {
      DraggableSheet(
        title = localizedString(R.string.create_loan),
        error = error,
        onDismiss = { viewModel.toggleShowCreateSheet() }
      ) {
        InputField(
          value = name,
          onValueChange = { viewModel.setName(it)},
          imeAction = ImeAction.Next,
          label = localizedString(R.string.full_name),
          leading = painterResource(R.drawable.person)
        )
        InputField(
          value = amount,
          onValueChange = { viewModel.setAmount(it)},
          imeAction = ImeAction.Done,
          onImeAction = { viewModel.createLoan()},
          label = localizedString(R.string.amount),
          keyboardType = KeyboardType.Number,
          leading = painterResource(R.drawable.money)
        )
        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = Spacings.sm()),
          horizontalArrangement = Arrangement.End
        ) {
          MyButton(text = localizedString(R.string.add_now)) { viewModel.createLoan() }
        }
      }
    }
    if (showAlert && selectedLoan != null) {
      AlertDialogModal(loan = selectedLoan!!, viewModel = viewModel) {viewModel.toggleShowAlert()}
    }
    when {
      filteredLoans.isEmpty() -> {
        Column(
          modifier = Modifier
            .fillMaxWidth(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Card(
            modifier = Modifier
              .padding(Spacings.lg())
          ) {
            Text(
              text = localizedString(R.string.no_loan),
              color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
              modifier = Modifier.padding(Spacings.lg())
            )
          }
        }
      }
      else -> {
        LazyColumn {
          items(filteredLoans) {loan->
            LoanCard(
              loan = loan,
              context = context,
              onClick = { selectedLoan = loan; viewModel.toggleShowAlert() },
              onDelete = { viewModel.deleteLoan(loan)}
            )
          }
        }
      }
    }
  }
}

