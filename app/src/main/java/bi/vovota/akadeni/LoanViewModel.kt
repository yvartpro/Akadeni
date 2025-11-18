package bi.vovota.akadeni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bi.vovota.akadeni.data.local.AppPrefsManager
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.data.local.model.LoanStatus
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.ui.screen.LoanCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoanViewModel(
    private val dao: LoanRepo,
    private val appPrefs: AppPrefsManager
): ViewModel() {
  private val _loans = MutableStateFlow<List<Loan>>(emptyList())
  val loans = _loans.asStateFlow()

  private val _filter = MutableStateFlow<LoanFilter?>(null)
  val filter = _filter.asStateFlow()
  
  private val _updateAction = MutableStateFlow<LoanAction>(LoanAction.PAY)
  val updateAction = _updateAction.asStateFlow()
  
  fun setActionPay(){
    _updateAction.value = LoanAction.PAY
  }

  fun setActionAdd(){
    _updateAction.value = LoanAction.INCREASE
  }

  fun setHistory() {
    _filter.value = LoanFilter.NOT_PAID
  }

  fun setPartPaid() {
    _filter.value = LoanFilter.PART_PAID
  }
  fun setDeleted() {
    _filter.value = LoanFilter.DELETED
  }
  fun setPaid() {
    _filter.value = LoanFilter.PAID
  }

  fun setAllLoans(){
    _filter.value = LoanFilter.ALL
  }
  private val _name = MutableStateFlow("")
  val name = _name.asStateFlow()

  private val _amount = MutableStateFlow("")
  val amount = _amount.asStateFlow()

  private val _inputAmount = MutableStateFlow("")
  val inputAmount = _inputAmount.asStateFlow()

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
  fun setinputAmount(value: String) = _inputAmount.tryEmit(value)
  fun setQuery(value: String) = _query.tryEmit(value)

  fun clearForm() {
    _name.value = ""
    _amount.value = ""
    _inputAmount.value = ""
  }
  fun toggleShowCreateSheet() {
    _isCreateSheetVisible.value = !_isCreateSheetVisible.value
    _error.value = ""
  }
  fun toggleShowAlert(loan: Loan?) {
    _isAlertVisible.value = !_isAlertVisible.value
    loan?.let {
      val remainder = it.amount - it.paid
      _inputAmount.value = remainder.toString()
    }
    _error.value = ""
  }
  fun toggleSearchMode() {
    _searchMode.value = !_searchMode.value
    _query.value = ""
  }

  init {
    viewModelScope.launch {
      getLoans()
    }
  }

  val lang = appPrefs.locale.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Companion.WhileSubscribed(5000),
    initialValue = "rn"
  )

  fun setLocale(newLang: String) {
    viewModelScope.launch {
      appPrefs.saveLocale(newLang)
    }
  }

  fun getLoans() {
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
      dao.createLoan(
          Loan(
              name = _name.value,
              amount = _amount.value.toDouble()
          )
      )
      clearForm()
      toggleShowCreateSheet()
      _error.value = ""
    }
  }

  fun deleteLoan(loan: Loan) {
    viewModelScope.launch {
      val deleted = loan.copy(isDeleted = true)
      dao.updateLoan(deleted)
    }
  }

  fun updateLoan(loan: Loan) {
    if (_inputAmount.value.isBlank()) {
      _error.value = "Please enter a valid amount"
      return
    }
    viewModelScope.launch {
      val inputAmount = loan.paid + _inputAmount.value.toDouble()
      val newAmount = loan.amount + _inputAmount.value.toDouble()
      val updatedLoan = loan.copy(
        paid = if (_updateAction.value == LoanAction.PAY) inputAmount else loan.paid,
        amount = if (_updateAction.value == LoanAction.INCREASE) newAmount else loan.amount,
        updatedAt = System.currentTimeMillis(),
        status = when {
          loan.amount > inputAmount -> LoanStatus.PARTIAL
          loan.amount <= inputAmount -> LoanStatus.PAID
          else -> LoanStatus.PENDING
        }
      )
      dao.updateLoan(updatedLoan)
      clearForm()
      toggleShowAlert(null)
      _error.value = ""
    }
  }
}

enum class LoanFilter { ALL, PAID, DELETED, PART_PAID, NOT_PAID}
enum class LoanAction { PAY, INCREASE }
