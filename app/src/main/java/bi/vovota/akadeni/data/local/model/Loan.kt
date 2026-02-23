package bi.vovota.akadeni.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan")
data class Loan(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val amount: Double,
  val paid: Double = 0.0,
  val status: LoanStatus = LoanStatus.NOT_PAID,
  val updatedAt: Long = System.currentTimeMillis(),
  val createdAt: Long = System.currentTimeMillis(),
  /**
   * The date/time (epoch millis) when this debt is expected to be paid.
   * A [DebtReminderWorker] will fire a notification at this exact moment.
   * Null means no reminder is scheduled.
   */
  val dueDate: Long? = null
)

enum class LoanStatus { PAID, PARTIAL, NOT_PAID}