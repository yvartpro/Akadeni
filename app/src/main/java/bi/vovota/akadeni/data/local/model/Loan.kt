package bi.vovota.akadeni.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan")
data class Loan(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val amount: Float,
  val status: LoanStatus = LoanStatus.PENDING,
  val createdAt: Long = System.currentTimeMillis()
)

enum class LoanStatus { PENDING, PAID, PARTIAL}