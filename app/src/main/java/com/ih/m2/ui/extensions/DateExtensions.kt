package com.ih.m2.ui.extensions

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.ui.utils.EMPTY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val EEEE = "EEEE"
const val DD = "dd"
const val MMM = "MMM"
const val EEE_MMM_DD_YYYY = "EEE, MMM dd, yyyy, HH:mm a z"
const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.ss'Z'"
const val NORMAL_FORMAT = "yyyy-MM-dd HH:mm:ss"

val Date.YYYY_MM_DD_HH_MM_SS: String
    get() = SimpleDateFormat(NORMAL_FORMAT, Locale.getDefault()).format(this)


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
        SimpleDateFormat(EEE_MMM_DD_YYYY, Locale.US).format(this).toString()

val Date.dayOfWeek: String get() = SimpleDateFormat(EEEE, Locale.getDefault()).format(this)
val Date.dayOfMonth: String get() = SimpleDateFormat(DD, Locale.getDefault()).format(this)
val Date.nameOfMonth: String get() = SimpleDateFormat(MMM, Locale.getDefault()).format(this)


fun String?.toFormatDate(): String {
    return try {
        this?.toDate(ISO_FORMAT)?.DayAndDateWithYear.orEmpty()
    } catch (e: Exception) {
        this?.toDate(NORMAL_FORMAT)?.DayAndDateWithYear.orEmpty()
    }

}

fun Date.timeStamp() = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(this)