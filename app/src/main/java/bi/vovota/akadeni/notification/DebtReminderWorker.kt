package bi.vovota.akadeni.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * WorkManager worker that fires a single debt-due notification.
 *
 * Design decisions:
 * - Extends [CoroutineWorker] (not [Worker]) so any future async work (e.g. DB query)
 *   runs on the IO dispatcher without blocking the main thread.
 * - All required data is packed into [WorkerParameters.inputData] before enqueueing,
 *   so the worker is self-contained and does NOT need to open the DB.
 *   This makes it resilient to process death: WorkManager persists the input data in
 *   its own SQLite table and retries with the same data after process restart.
 * - Returns [Result.retry] on unexpected exceptions so WorkManager will reschedule
 *   with exponential back-off rather than silently drop the notification.
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
            val loanId     = inputData.getInt(KEY_LOAN_ID, -1)
            val personName = inputData.getString(KEY_PERSON_NAME) ?: return Result.failure()
            val amount     = inputData.getDouble(KEY_AMOUNT, 0.0)

            if (loanId == -1) return Result.failure()  // malformed data – do not retry

            NotificationHelper.showReminder(
                context     = ctx,
                loanId      = loanId,
                personName  = personName,
                amount      = amount,
            )
            Result.success()
        } catch (e: Exception) {
            // Retry on transient failures (e.g. notification service unavailable at exact moment).
            Result.retry()
        }
    }
}
