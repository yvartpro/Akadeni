package bi.vovota.akadeni.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.data.local.model.LoanStatus
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoanViewModel(private val dao: LoanRepo): ViewModel() {
  private val _loans = MutableStateFlow<List<Loan>>(emptyList())
  val loans = _loans.asStateFlow()

  private val _name = MutableStateFlow("")
  val name = _name.asStateFlow()

  private val _amount = MutableStateFlow("")
  val amount = _amount.asStateFlow()

  private val _newAmount = MutableStateFlow("")
  val newAmount = _newAmount.asStateFlow()

  private val _query = MutableStateFlow("")
  val query = _query.asStateFlow()

  private val _searchMode = MutableStateFlow(false)
  val searchMode =  _searchMode.asStateFlow()

  private val _error = MutableStateFlow("")
  val error = _error.asStateFlow()

  private val _isCreateSheetVisible = MutableStateFlow(false)
  val isCreateSheetVisible = _isCreateSheetVisible.asStateFlow()

  private val _isAlertVisible = MutableStateFlow(false)
  val isAlertVisible = _isAlertVisible.asStateFlow()

  fun setName(value: String) = _name.tryEmit(value)
  fun setAmount(value: String) = _amount.tryEmit(value)
  fun setNewAmount(value: String) = _newAmount.tryEmit(value)
  fun setQuery(value: String) = _query.tryEmit(value)

  fun clearForm() {
    _name.value = ""
    _amount.value = ""
    _newAmount.value = ""
  }
  fun toggleShowCreateSheet() {
    _isCreateSheetVisible.value = !_isCreateSheetVisible.value
    _error.value = ""
  }
  fun toggleShowAlert() {
    _isAlertVisible.value = !_isAlertVisible.value
    _error.value = ""
  }
  fun toggleSearchMode() {
    _searchMode.value = !_searchMode.value
    _query.value = ""
  }

  init {
    viewModelScope.launch {
      dao.getLoans().collect { _loans.value = it }
    }
  }

  fun createLoan() {
    if (_name.value.isBlank() || _amount.value.isBlank()) {
      _error.value = "All fields are required"
      return
    }
    viewModelScope.launch {
      dao.createLoan(Loan(
        name = _name.value,
        amount = _amount.value.toDouble()
      ))
      clearForm()
      toggleShowCreateSheet()
      _error.value = ""
    }
  }

  fun deleteLoan(loan: Loan) {
    viewModelScope.launch {
      dao.deleteLoan(loan)
    }
  }

  fun updateLoan(loan: Loan) {
    if (_newAmount.value.isBlank()) {
      _error.value = "Please enter a valid amount"
      return
    }
    viewModelScope.launch {
      val paidAmount = loan.paid + _newAmount.value.toDouble()
      val updatedLoan = loan.copy(
        paid = paidAmount,
        updatedAt = System.currentTimeMillis(),
        status = when {
          loan.amount > paidAmount -> LoanStatus.PARTIAL
          loan.amount <= paidAmount -> LoanStatus.PAID
          else -> LoanStatus.PENDING
        }
      )
      dao.updateLoan(updatedLoan)
      clearForm()
      toggleShowAlert()
      _error.value = ""
    }
  }
}