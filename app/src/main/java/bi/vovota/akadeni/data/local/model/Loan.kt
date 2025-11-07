package bi.vovota.akadeni.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan")
data class Loan(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val amount: Double,
  val paid: Double = 0.0,
  val status: LoanStatus = LoanStatus.PENDING,
  val updatedAt: Long = System.currentTimeMillis(),
  val createdAt: Long = System.currentTimeMillis()
)

enum class LoanStatus { PENDING, PAID, PARTIAL}