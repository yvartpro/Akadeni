package bi.vovota.akadeni.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ConstantLocale")
fun formatDate(context: Context, dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    val utc = TimeZone.getTimeZone("UTC")
    val locale = Locale.getDefault()

    val possibleFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    )

    var date: Date? = null
    for (pattern in possibleFormats) {
        try {
            val parser = SimpleDateFormat(pattern, locale).apply { timeZone = utc }
            date = parser.parse(dateString)
            if (date != null) break
        } catch (_: Exception) { }
    }

    if (date == null) return ""

    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { time = date }

    // compute difference in milliseconds for minute/hour wording
    val diffMillis = now.timeInMillis - then.timeInMillis
    val diffSeconds = diffMillis / 1000
    val diffMinutes = diffSeconds / 60
    val diffHours = diffMinutes / 60

    // compute calendar-aware day difference (zero midnight-to-midnight)
    fun daysBetween(a: Calendar, b: Calendar): Long {
        val ca = a.clone() as Calendar
        val cb = b.clone() as Calendar
        ca.set(Calendar.HOUR_OF_DAY, 0); ca.set(Calendar.MINUTE, 0); ca.set(Calendar.SECOND, 0); ca.set(Calendar.MILLISECOND, 0)
        cb.set(Calendar.HOUR_OF_DAY, 0); cb.set(Calendar.MINUTE, 0); cb.set(Calendar.SECOND, 0); cb.set(Calendar.MILLISECOND, 0)
        val diff = cb.timeInMillis - ca.timeInMillis
        return diff / (24 * 60 * 60 * 1000)
    }

    val dayDiff = daysBetween(then, now)

    // respect device 24h setting
    val is24Hour = DateFormat.is24HourFormat(context)
    val timePattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val outputTime = SimpleDateFormat(timePattern, locale)
    val shortDate = SimpleDateFormat("EEE, dd MMM", locale)
    val longDate = SimpleDateFormat("dd MMM yyyy", locale)

    return when {
        diffSeconds < 10 -> "maintenant"
        diffSeconds < 60 -> "il y a quelques secondes"
        diffMinutes < 60 -> {
            val m = diffMinutes.coerceAtLeast(1)
            "il y a $m minute${if (m > 1) "s" else ""}"
        }
        diffHours < 24 && dayDiff == 0L -> {
            val h = diffHours.coerceAtLeast(1)
            "il y a $h heure${if (h > 1) "s" else ""}"
        }
        dayDiff == 1L -> "Hier à ${outputTime.format(date)}"
        dayDiff in 0..6 -> "il y a $dayDiff jour${if (dayDiff > 1) "s" else ""}"
        dayDiff in 7..364 -> shortDate.format(date)
        else -> longDate.format(date)
    }
}

/** Extract year (e.g. "2025") from the same possible date strings. Returns empty string on failure */
fun dateToYear(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""
    val possibleFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd"
    )
    for (pattern in possibleFormats) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
            val d = parser.parse(dateString)
            if (d != null) {
                val cal = Calendar.getInstance().apply { time = d }
                return cal.get(Calendar.YEAR).toString()
            }
        } catch (_: Exception) { }
    }
    return ""
}