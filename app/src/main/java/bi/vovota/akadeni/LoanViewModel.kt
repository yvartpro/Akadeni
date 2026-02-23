package bi.vovota.akadeni

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bi.vovota.akadeni.data.local.AppPrefsManager
import bi.vovota.akadeni.data.local.model.Loan
import bi.vovota.akadeni.data.local.model.LoanStatus
import bi.vovota.akadeni.data.repo.LoanRepo
import bi.vovota.akadeni.notification.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for loan management.
 *
 * Extends [AndroidViewModel] instead of [ViewModel] so we can safely obtain the
 * Application context for WorkManager operations without leaking an Activity context.
 */
class LoanViewModel(
    application: Application,
    private val dao: LoanRepo,
    private val appPrefs: AppPrefsManager
): AndroidViewModel(application) {

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
    _inputAmount.value = ""
  }

  fun setHistory() {
    _filter.value = LoanFilter.NOT_PAID
  }

  fun setPartPaid() {
    _filter.value = LoanFilter.PART_PAID
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

  /**
   * Holds the selected due-date (epoch millis) for the loan being created/edited.
   * Null means the user has not selected a reminder date.
   */
  private val _dueDate = MutableStateFlow<Long?>(null)
  val dueDate = _dueDate.asStateFlow()

  fun setName(value: String) = _name.tryEmit(value)
  fun setAmount(value: String) = _amount.tryEmit(value)
  fun setInputAmount(value: String) = _inputAmount.tryEmit(value)
  fun setQuery(value: String) = _query.tryEmit(value)
  /** Called from the date picker in the UI when the user selects a due date. */
  fun setDueDate(millis: Long?) { _dueDate.value = millis }

  fun clearForm() {
    _name.value = ""
    _amount.value = ""
    _inputAmount.value = ""
    _dueDate.value = null   // reset date picker selection
  }
  fun toggleShowCreateSheet() {
    _isCreateSheetVisible.value = !_isCreateSheetVisible.value
    _error.value = ""
  }
  fun toggleShowAlert(loan: Loan?) {
    _isAlertVisible.value = !_isAlertVisible.value
    loan?.let {
      val remainder = it.amount - it.paid
      _inputAmount.value =
        if (remainder % 1 == 0.0) remainder.toInt().toString()
        else remainder.toString()
      // Pre-fill date picker with existing dueDate so the user can update it
      _dueDate.value = it.dueDate
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
      val newLoan = Loan(
        name     = _name.value,
        amount   = _amount.value.toDouble(),
        dueDate  = _dueDate.value  // may be null if no date was chosen
      )
      dao.createLoan(newLoan)

      // If a dueDate was selected, schedule a reminder.
      // We re-query to get the auto-generated id from Room.
      // Using a snapshot is safe here since we just inserted the row.
      val inserted = _loans.value.firstOrNull {
        it.name == newLoan.name && it.createdAt == newLoan.createdAt
      }
      inserted?.let { ReminderScheduler.scheduleReminder(getApplication(), it) }

      clearForm()
      toggleShowCreateSheet()
      _error.value = ""
    }
  }

  fun deleteLoan(loan: Loan) {
    viewModelScope.launch {
      // Cancel any pending WorkManager reminder BEFORE deleting to avoid orphaned workers.
      ReminderScheduler.cancelReminder(getApplication(), loan.id)
      dao.deleteLoan(loan)
    }
  }

  fun updateLoan(loan: Loan) {
    if (_inputAmount.value.isBlank()) {
      _error.value = "Please enter a valid amount"
      setActionPay()
      return
    }

    viewModelScope.launch {
      val delta = _inputAmount.value.toDouble()
      var newPaid = loan.paid
      var newAmount = loan.amount

      when (_updateAction.value) {
        LoanAction.PAY -> {
          newPaid = loan.paid + delta
        }
        LoanAction.INCREASE -> {
          newAmount = loan.amount + delta
        }
      }
      // determine status
      val newStatus = when {
        newPaid == 0.0 -> LoanStatus.NOT_PAID
        newPaid < newAmount -> LoanStatus.PARTIAL
        newPaid == newAmount -> LoanStatus.PAID
        else -> LoanStatus.PAID
      }

      val updatedLoan = loan.copy(
        amount    = newAmount,
        paid      = newPaid,
        status    = newStatus,
        updatedAt = System.currentTimeMillis(),
        dueDate   = _dueDate.value   // update due date if the user changed it
      )

      dao.updateLoan(updatedLoan)

      // REPLACE policy in ReminderScheduler handles rescheduling automatically
      // when dueDate changes, and cancels correctly if dueDate is now null or past.
      ReminderScheduler.scheduleReminder(getApplication(), updatedLoan)

      clearForm()
      toggleShowAlert(null)
      _updateAction.value = LoanAction.PAY
      _error.value = ""
    }
  }

}

enum class LoanFilter { ALL, PAID, PART_PAID, NOT_PAID}
enum class LoanAction { PAY, INCREASE }
