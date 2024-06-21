package com.ih.m2.ui.extensions

import android.util.Log
import com.ih.m2.ui.utils.EMPTY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val EEEE = "EEEE"
const val DD = "dd"
const val MMM = "MMM"
const val EEE_MMM_DD_YYYY = "EEE, MMM dd, yyyy, HH:mm a z"

val Date.YYYY_MM_DD_HH_MM_SS: String
    get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)


fun String.toDate(): Date? {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'", Locale.getDefault())
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
    return this?.toDate()?.DayAndDateWithYear ?: EMPTY
}