package com.ih.m2.ui.extensions

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.encoders.annotations.Encodable.Ignore
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
const val TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss"

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
        SimpleDateFormat(EEE_MMM_DD_YYYY, Locale.getDefault()).format(this).toString()

val Date.dayOfWeek: String get() = SimpleDateFormat(EEEE, Locale.getDefault()).format(this)
val Date.dayOfMonth: String get() = SimpleDateFormat(DD, Locale.getDefault()).format(this)
val Date.nameOfMonth: String get() = SimpleDateFormat(MMM, Locale.getDefault()).format(this)


fun String?.toFormatDate(format: String): String {
    return try {
        if (this.isNullOrEmpty() || this.isBlank() || this.isEmpty()) return EMPTY
        this.toDate(format)?.DayAndDateWithYear.orEmpty()
    } catch (e: Exception) {
        Log.e("test","Exception ${e.localizedMessage}")
        FirebaseCrashlytics.getInstance().recordException(e)
        EMPTY
    }
}

fun Date.timeStamp(): String = SimpleDateFormat(TIME_STAMP_FORMAT, Locale.getDefault()).format(this)