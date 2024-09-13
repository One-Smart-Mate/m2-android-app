package com.ih.osm.ui.extensions

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.R
import com.ih.osm.ui.utils.EMPTY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val EEEE = "EEEE"
const val DD = "dd"
const val MMM = "MMM"
const val EEE_MMM_DD_YYYY = "EEE, MMM dd, yyyy, HH:mm a z"

const val EEE_MMM_DD_HH_MM_A = "EEE, MMM dd HH:mm a"

const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.ss'Z'"
const val NORMAL_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val SIMPLE_DATE_FORMAT = "yyyy-MM-dd"
const val TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss"

val Date.YYYY_MM_DD_HH_MM_SS: String
    get() = SimpleDateFormat(NORMAL_FORMAT, Locale.getDefault()).format(this)

val Date.DayMonthWithTimeZone: String
    get() = SimpleDateFormat(EEE_MMM_DD_HH_MM_A, Locale.getDefault()).format(this)

fun String.toDate(format: String): Date? {
    return SimpleDateFormat(format, Locale.getDefault())
        .parse(this)
}

fun Date.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        setTime(this.time)
    }
}

val Date.DayAndDateWithYear: String
    get() =
        SimpleDateFormat(EEE_MMM_DD_YYYY, Locale.getDefault()).format(this).toString()

val Date.dayOfWeek: String get() = SimpleDateFormat(EEEE, Locale.getDefault()).format(this)
val Date.dayOfMonth: String get() = SimpleDateFormat(DD, Locale.getDefault()).format(this)
val Date.nameOfMonth: String get() = SimpleDateFormat(MMM, Locale.getDefault()).format(this)

fun String?.toFormatDate(format: String): String {
    return try {
        if (this.isNullOrEmpty() || this.isBlank() || this.isEmpty()) return EMPTY
        this.toDate(format)?.DayAndDateWithYear.orEmpty()
    } catch (e: Exception) {
        Log.e("test", "Exception ${e.localizedMessage}")
        FirebaseCrashlytics.getInstance().recordException(e)
        this.orEmpty()
    }
}

fun Date.timeStamp(): String = SimpleDateFormat(TIME_STAMP_FORMAT, Locale.getDefault()).format(this)

fun String.lastSyncDate(context: Context): String {
    try {
        val lastSync = this.toDate(NORMAL_FORMAT)
        val now = Calendar.getInstance().time
        val diff: Long = now.time - lastSync?.time.defaultIfNull(0L)
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if (days != 0L) {
            val dayLabel =
                if (days <= 1) {
                    context.getString(R.string.day)
                } else {
                    context.getString(R.string.days)
                }
            return "$days $dayLabel"
        }
        if (hours != 0L) {
            val hourLabel =
                if (hours <= 1) {
                    context.getString(R.string.hour)
                } else {
                    context.getString(R.string.hours)
                }

            return "$hours $hourLabel"
        }
        if (minutes != 0L) {
            val minutesValue =
                if (minutes <= 1) {
                    context.getString(R.string.minute)
                } else {
                    context.getString(R.string.minutes)
                }
            return "$minutes $minutesValue"
        }
        return context.getString(R.string.right_now)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
    return EMPTY
}

fun String.isExpired(): Boolean {
    if (this.isEmpty() || this.isBlank()) return false
    val dueDate = this.toDate(SIMPLE_DATE_FORMAT)
    val todayDate = Calendar.getInstance().time
    return dueDate?.before(todayDate).defaultIfNull(false)
}
