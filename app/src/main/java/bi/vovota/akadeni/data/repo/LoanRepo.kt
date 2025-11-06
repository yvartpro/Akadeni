package bi.vovota.akadeni.data.repo

import bi.vovota.akadeni.data.local.LoanDao
import bi.vovota.akadeni.data.local.model.Loan

class LoanRepo(private val loanDao: LoanDao) {
  fun getLoans() = loanDao.getAllLoans()
  suspend fun createLoan(loan: Loan) = loanDao.createLoan(loan)
}