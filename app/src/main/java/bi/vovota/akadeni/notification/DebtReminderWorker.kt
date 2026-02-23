package bi.vovota.akadeni.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import bi.vovota.akadeni.data.local.AppDatabase
import bi.vovota.akadeni.data.local.model.LoanStatus
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that fires a debt-due notification and reschedules every 5 days.
 *
 * Design decisions:
 * - Extends [CoroutineWorker] to run DB operations on IO dispatcher.
 * - Fetches the latest loan data from DB to ensure status is up-to-date.
 * - If still unpaid, shows notification and schedules the next one (5 days later).
 * - Updates the loan's [dueDate] in the database to reflect the next reminder.
 */
class DebtReminderWorker(
    private val ctx: Context,
    params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    companion object {
        /** WorkManager input data keys – use constants to avoid typos. */
        const val KEY_LOAN_ID     = "loan_id"
        const val KEY_PERSON_NAME = "person_name"
        const val KEY_AMOUNT      = "amount"
    }

    override suspend fun doWork(): Result {
        return try {
            val loanId = inputData.getInt(KEY_LOAN_ID, -1)
            if (loanId == -1) return Result.failure()

            val dao = AppDatabase.getDatabase(ctx).loanDao()
            val loan = dao.getLoanById(loanId) ?: return Result.failure()

            // 1. If the debt is already fully paid, we stop the cycle.
            if (loan.status == LoanStatus.PAID) {
                return Result.success()
            }

            // 2. Show the immediate notification.
            NotificationHelper.showReminder(
                context     = ctx,
                loanId      = loanId,
                personName  = loan.name,
                amount      = loan.amount,
            )

            // 3. Schedule the recurrent reminder (5 days from now).
            // We update the database so the UI also shows the next planned notification.
            val nextDueDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5)
            val updatedLoan = loan.copy(dueDate = nextDueDate)
            
            dao.updateLoan(updatedLoan)
            ReminderScheduler.scheduleReminder(ctx, updatedLoan)

            Result.success()
        } catch (e: Exception) {
            // Retry on transient failures (e.g. notification service unavailable).
            Result.retry()
        }
    }
}
