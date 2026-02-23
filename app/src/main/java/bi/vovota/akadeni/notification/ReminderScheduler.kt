package bi.vovota.akadeni.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import bi.vovota.akadeni.data.local.model.Loan
import java.util.concurrent.TimeUnit

/**
 * Schedules and cancels WorkManager jobs for debt payment reminders.
 *
 * Design decisions:
 * - [ExistingWorkPolicy.REPLACE]: updating the dueDate on an existing loan
 *   automatically cancels the old worker and schedules a new one — no
 *   manual cancel-then-schedule needed at the call site.
 * - Unique work name is deterministic from [Loan.id], so only ever one
 *   reminder per loan can exist in WorkManager's queue at a time.
 * - If dueDate is null or already in the past (delay ≤ 0), we skip silently.
 *   We do NOT fire an immediate notification — the user can add a date later.
 */
object ReminderScheduler {

    /**
     * Unique work name pattern: `debt_reminder_<loanId>`.
     * Stable across app restarts so cancel-by-name always works.
     */
    private fun workName(loanId: Int) = "debt_reminder_$loanId"

    /**
     * Schedules (or replaces) the single reminder for [loan].
     *
     * Does nothing if [Loan.dueDate] is null or is in the past.
     *
     * @param context Application context (safe to pass from ViewModel via Application).
     * @param loan    The loan whose [Loan.dueDate] is used for scheduling.
     */
    fun scheduleReminder(context: Context, loan: Loan) {
        val dueDate = loan.dueDate ?: return  // no date set → nothing to schedule
        val delayMillis = dueDate - System.currentTimeMillis()

        if (delayMillis <= 0) return  // already past → skip silently

        val inputData = Data.Builder()
            .putInt(DebtReminderWorker.KEY_LOAN_ID, loan.id)
            .putString(DebtReminderWorker.KEY_PERSON_NAME, loan.name)
            .putDouble(DebtReminderWorker.KEY_AMOUNT, loan.amount)
            .build()

        val request = OneTimeWorkRequestBuilder<DebtReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName(loan.id),
            ExistingWorkPolicy.REPLACE,  // replaces old reminder if dueDate changed
            request
        )
    }

    /**
     * Cancels any pending reminder for the loan identified by [loanId].
     * Safe to call even if no reminder exists (no-op).
     * Call this when a loan is deleted so orphan workers are cleaned up.
     */
    fun cancelReminder(context: Context, loanId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(loanId))
    }
}
