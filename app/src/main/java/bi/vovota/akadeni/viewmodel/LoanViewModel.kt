package bi.vovota.akadeni.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoanViewModel(private val repo: LoanRepo): ViewModel() {
  private val _loans = MutableStateFlow<List<Loan>>(emptyList())
  val loans = _loans.asStateFlow()

  private val _name = MutableStateFlow("")
  val name = _name.asStateFlow()

  private val _amount = MutableStateFlow(0f)
  val amount = _amount.asStateFlow()

  private val _isCreateSheetVisible = MutableStateFlow(false)
  val isCreateSheetVisible = _isCreateSheetVisible.asStateFlow()

  fun setName(value: String) = _name.tryEmit(value)
  fun setAmount(value: String) = _amount.tryEmit(value.toFloat())
  fun toggleShowCreateSheet() {
    Logger.d("Sheet", _isCreateSheetVisible.value.toString())
    _isCreateSheetVisible.value = !_isCreateSheetVisible.value
  }

  init {
    viewModelScope.launch {
      repo.getLoans().collect { _loans.value = it }
    }
  }

  fun createLoan() {
    viewModelScope.launch {
      repo.createLoan(Loan(
        name = _name.value,
        amount = _amount.value
      ))
      Logger.d("Create Loan", "created")
    }
  }
}