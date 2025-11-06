package bi.vovota.akadeni.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bi.vovota.akadeni.utils.formatDate
import bi.vovota.akadeni.ui.components.DetailText
import bi.vovota.akadeni.ui.components.InputField
import bi.vovota.akadeni.ui.components.SmallText
import bi.vovota.akadeni.ui.components.TitleText
import bi.vovota.akadeni.ui.theme.Spacings
import bi.vovota.akadeni.viewmodel.LoanViewModel
import bi.vovota.akadeni.R
import bi.vovota.akadeni.ui.components.DraggableSheet
import bi.vovota.akadeni.utils.Logger

@Composable
fun WelcomeScreen(viewModel: LoanViewModel) {
  val loans by viewModel.loans.collectAsState()
  val name by viewModel.name.collectAsState()
  val amount by viewModel.amount.collectAsState()
  val isSheetVisible by viewModel.isCreateSheetVisible.collectAsState()

  val context = LocalContext.current

  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    if (isSheetVisible) {
      DraggableSheet(
        title = "Create loan",
        onDismiss = { viewModel.toggleShowCreateSheet() }
      ) {
        InputField(
          value = name,
          onValueChange = { viewModel.setName(it)},
          imeAction = ImeAction.Next,
          label = "Full name",
          leading = R.drawable.person
        )
        InputField(
          value = amount.toString(),
          onValueChange = { viewModel.setAmount(it)},
          imeAction = ImeAction.Done,
          onImeAction = { viewModel.createLoan()},
          label = "Amount",
          keyboardType = KeyboardType.Decimal,
          leading = R.drawable.money
        )
        Button(onClick = { viewModel.createLoan() }) {
          SmallText(text = "Create")
        }
      }
    }

    TextButton(onClick = { viewModel.toggleShowCreateSheet()
      Logger.d("Loans", loans.toString())
    }) {
      TitleText(value = "All loans")
    }
    when {
      loans.isEmpty() -> {
        SmallText("No loans yet")
      }
      else -> {
        LazyColumn {
          items(loans) {loan->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              shape = RoundedCornerShape(Spacings.xs()),
              colors = CardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
              )
            ) {
              DetailText(value = "Full name: ${loan.name}")
              Spacer(Modifier.height(Spacings.xs()))
              DetailText(value = "Amount: ${loan.amount}")
              SmallText(text = formatDate(context, loan.createdAt))
            }
          }
        }
      }
    }
  }
}