package com.ih.osm.ui.extensions

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.R
import com.ih.osm.domain.model.ExecutionStatus
import com.ih.osm.ui.utils.EMPTY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val EEEE = "EEEE"
const val DD = "dd"
const val MMM = "MMM"
const val EEE_MMM_DD_YYYY = "EEE, MMM dd, yyyy, HH:mm a z"

const val EEE_MMM_DD_HH_MM_A = "EEE, MMM dd HH:mm a"

const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.ss'Z'"
const val NORMAL_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val SIMPLE_DATE_FORMAT = "yyyy-MM-dd"
const val TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss"
const val DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm"
const val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

val Date.YYYY_MM_DD_HH_MM_SS: String
    get() = SimpleDateFormat(NORMAL_FORMAT, Locale.getDefault()).format(this)

val Date.DayMonthWithTimeZone: String
    get() = SimpleDateFormat(EEE_MMM_DD_HH_MM_A, Locale.getDefault()).format(this)

fun String.toDate(
    format: String,
    timeZone: TimeZone = TimeZone.getDefault(),
): Date? {
    return SimpleDateFormat(format, Locale.getDefault()).apply {
        this.timeZone = timeZone
    }.parse(this)
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

fun String.isCardExpired(referenceDateString: String): Boolean {
    if (this.isEmpty() || this.isBlank()) return false
    val dueDate = this.toDate(SIMPLE_DATE_FORMAT)
    val referenceDate = referenceDateString.toDate(ISO, TimeZone.getTimeZone("UTC"))
    return dueDate?.before(referenceDate).defaultIfNull(false)
}

fun String?.fromIsoToFormattedDate(
    inputPattern: String = ISO,
    outputPattern: String = DD_MM_YYYY_HH_MM,
): String {
    if (this.isNullOrBlank()) return ""

    return try {
        val inputFormat =
            SimpleDateFormat(inputPattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        val date = inputFormat.parse(this) ?: return ""
        val outputFormat =
            SimpleDateFormat(outputPattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun String?.fromIsoToNormalDate(): String {
    if (this.isNullOrBlank()) return EMPTY

    return try {
        val isoFormat =
            SimpleDateFormat(ISO, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val date = isoFormat.parse(this)

        val outputFormat =
            SimpleDateFormat(NORMAL_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }

        date?.let { outputFormat.format(it) } ?: EMPTY
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        EMPTY
    }
}

fun getCurrentDate(): String {
    return SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault()).format(Date())
}

fun getCurrentDateTimeUtc(): String {
    val sdf = SimpleDateFormat(ISO, Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

fun String?.toHourFromIso(): String {
    if (this.isNullOrBlank()) return EMPTY

    return try {
        val isoFormat =
            SimpleDateFormat(ISO, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val date = isoFormat.parse(this)

        val outputFormat =
            SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }

        date?.let { outputFormat.format(it) } ?: EMPTY
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        EMPTY
    }
}

fun Int.toMinutesAndSeconds(): String {
    if (this <= 0) return "0 s"
    val minutes = this / 60
    val seconds = this % 60
    return when {
        minutes > 0 && seconds > 0 -> "$minutes min $seconds s"
        minutes > 0 -> "$minutes min"
        else -> "$seconds s"
    }
}

fun calculateRemainingDaysFromIso(dueDateString: String): Int {
    return try {
        val sdf =
            SimpleDateFormat(ISO, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val dueDate = sdf.parse(dueDateString)
        val today = Date()

        val diffInMillis = dueDate.time - today.time
        (diffInMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        0
    }
}

fun String.isWithinExecutionWindow(
    context: Context,
    allowExecuteBefore: Boolean,
    allowExecuteBeforeMinutes: Int,
    toleranceBeforeMinutes: Int,
    toleranceAfterMinutes: Int,
    allowExecuteAfterDue: Boolean,
): Pair<Boolean, String?> {
    return try {
        val sdf =
            SimpleDateFormat(ISO, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val scheduleDate = sdf.parse(this) ?: return false to null
        val now = Date()

        val millisNow = now.time
        val millisSchedule = scheduleDate.time

        val effectiveBeforeMinutes =
            if (allowExecuteBefore) {
                allowExecuteBeforeMinutes + toleranceBeforeMinutes
            } else {
                toleranceBeforeMinutes
            }

        val beforeStartMillis =
            millisSchedule - (effectiveBeforeMinutes * 60 * 1000)
        val afterEndMillis = millisSchedule + (toleranceAfterMinutes * 60 * 1000)

        return when {
            millisNow in beforeStartMillis..afterEndMillis -> true to null
            millisNow < beforeStartMillis -> {
                val diff = beforeStartMillis - millisNow
                val mins = (diff / 1000 / 60).toInt()
                val secs = (diff / 1000 % 60).toInt()
                false to context.getString(R.string.execution_wait_time, mins, secs)
            }

            millisNow > afterEndMillis -> {
                if (allowExecuteAfterDue) {
                    true to null
                } else {
                    false to context.getString(R.string.sequence_out_of_time)
                }
            }

            else -> false to context.getString(R.string.cannot_execute_now)
        }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        false to context.getString(R.string.execution_error)
    }
}

fun String.getExecutionStatus(
    sequenceStart: String?,
    allowExecuteBefore: Boolean,
    allowExecuteBeforeMinutes: Int,
    toleranceBeforeMinutes: Int,
    toleranceAfterMinutes: Int,
    allowExecuteAfterDue: Boolean,
): ExecutionStatus {
    try {
        val sdf =
            SimpleDateFormat(ISO, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val scheduleDate = sdf.parse(this) ?: return ExecutionStatus.PENDING
        val now = Date()

        val millisNow = now.time
        val millisSchedule = scheduleDate.time

        val effectiveBeforeMinutes =
            if (allowExecuteBefore) {
                allowExecuteBeforeMinutes + toleranceBeforeMinutes
            } else {
                toleranceBeforeMinutes
            }

        val beforeStartMillis = millisSchedule - (effectiveBeforeMinutes * 60 * 1000)
        val afterEndMillis = millisSchedule + (toleranceAfterMinutes * 60 * 1000)

        if (sequenceStart == null) {
            return if (millisNow < beforeStartMillis) ExecutionStatus.PENDING else ExecutionStatus.PENDING
        }

        val executionStartDate = sdf.parse(sequenceStart) ?: return ExecutionStatus.PENDING
        val millisExecutionStart = executionStartDate.time

        return when {
            millisExecutionStart < beforeStartMillis -> ExecutionStatus.PENDING
            millisExecutionStart in beforeStartMillis until millisSchedule -> ExecutionStatus.PREMATURE
            millisExecutionStart in millisSchedule..afterEndMillis -> ExecutionStatus.ON_TIME
            millisExecutionStart > afterEndMillis -> ExecutionStatus.EXPIRED
            else -> ExecutionStatus.PENDING
        }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        return ExecutionStatus.PENDING
    }
}
