package bi.vovota.akadeni.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import bi.vovota.akadeni.MainActivity
import bi.vovota.akadeni.R

/**
 * Central helper for all debt reminder notifications.
 *
 * Design decisions:
 * - Channel is created once and is idempotent (safe to call on every app start).
 * - Notification ID is derived from loanId so it is stable across rescheduling:
 *   if the worker fires twice for the same loan (unlikely, but defensive), the
 *   second notification replaces the first rather than stacking.
 * - IMPORTANCE_HIGH ensures heads-up display on lock screen and ambient.
 */
object NotificationHelper {

    /** Notification channel ID – must be stable across app updates. */
    const val CHANNEL_ID = "debt_reminder_channel"

    /** Extra key passed in the PendingIntent so the host activity can navigate to the loan. */
    const val EXTRA_LOAN_ID = "extra_loan_id"

    /**
     * Registers the notification channel with the system.
     * Safe to call repeatedly (no-op if already registered).
     * Must be called before any notification is posted (we call it in [MainActivity.onCreate]).
     */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Posts the "debt is due" reminder notification.
     *
     * @param context     Application context.
     * @param loanId      Room primary key of the loan.
     * @param personName  Borrower's name shown in the notification title.
     * @param amount      Debt amount shown in the notification body.
     */
    fun showReminder(
        context: Context,
        loanId: Int,
        personName: String,
        amount: Double,
    ) {
        // PendingIntent opens MainActivity; the activity can read EXTRA_LOAN_ID to navigate.
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_LOAN_ID, loanId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            loanId, // use loanId as request code to make each PendingIntent unique
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(personName)
            .setContentText(context.getString(R.string.notification_body, amount))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)  // dismiss notification when tapped
            .build()

        // Stable notification ID = loanId; updating dueDate and rescheduling replaces this notification.
        NotificationManagerCompat.from(context).notify(loanId, notification)
    }
}
