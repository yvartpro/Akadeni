package bi.vovota.akadeni.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import bi.vovota.akadeni.data.local.model.Loan
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
  @Query("SELECT * FROM loan")
  fun getAllLoans(): Flow<List<Loan>>

  @Insert
  suspend fun createLoan(loan: Loan)

  @Delete
  suspend fun deleteLoan(loan: Loan)

  @Update
  suspend fun updateLoan(loan: Loan)


}