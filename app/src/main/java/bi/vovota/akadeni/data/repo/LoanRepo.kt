package bi.vovota.akadeni.data.repo

import bi.vovota.akadeni.data.local.LoanDao
import bi.vovota.akadeni.data.local.model.Loan

class LoanRepo(private val loanDao: LoanDao) {
  fun getLoans() = loanDao.getAllLoans()
  suspend fun createLoan(loan: Loan) = loanDao.createLoan(loan)
  suspend fun deleteLoan(loan: Loan) = loanDao.deleteLoan(loan) // deletion moved to update
  suspend fun updateLoan(loan: Loan) = loanDao.updateLoan(loan)
}