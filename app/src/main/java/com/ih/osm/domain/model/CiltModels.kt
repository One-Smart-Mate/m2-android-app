package com.ih.osm.domain.model

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.ih.osm.R
import com.ih.osm.ui.extensions.NORMAL_FORMAT
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.extensions.format
import com.ih.osm.ui.extensions.getMinutesDifference
import com.ih.osm.ui.extensions.parseUTCToLocal
import com.ih.osm.ui.extensions.toCalendar
import com.ih.osm.ui.extensions.toDate
import com.ih.osm.ui.extensions.toHourMinuteString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

data class CiltData(
    val userInfo: UserInfo,
    val positions: List<Position>,
)

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String,
)

data class Position(
    val id: Int,
    val name: String,
    val siteName: String,
    val areaName: String,
    val ciltMasters: List<CiltMaster>,
)

data class CiltMaster(
    val id: Int,
    val siteId: Int,
    val ciltName: String,
    val ciltDescription: String,
    val creatorId: Int,
    val creatorName: String,
    val reviewerId: Int,
    val reviewerName: String,
    val approvedById: Int,
    val approvedByName: String,
    val ciltDueDate: String,
    val standardTime: Int,
    val urlImgLayout: String?,
    val order: Int,
    val dateOfLastUsed: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
    val status: String,
    val sequences: List<Sequence>,
)

data class Sequence(
    val id: Int,
    val siteId: Int,
    val siteName: String,
    val ciltMstrId: Int,
    val ciltMstrName: String,
    val frecuencyId: Int,
    val frecuencyCode: String,
    val ciltTypeId: Int,
    val ciltTypeName: String,
    val secuenceList: String,
    val secuenceColor: String,
    val toolsRequired: String,
    val standardTime: Int,
    val standardOk: String,
    val referenceOplSopId: Int,
    val remediationOplSopId: String,
    val stoppageReason: Int,
    val machineStopped: Int,
    val quantityPicturesCreate: Int,
    val quantityPicturesClose: Int,
    val selectableWithoutProgramming: Int,
    val referencePoint: String?,
    val order: Int,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val executions: List<Execution>,
)

fun List<Execution>.sortByTime(): List<Execution> {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    return this
        .sortedBy { execution ->
            val date = execution.secuenceSchedule.parseUTCToLocal().toDate(format = NORMAL_FORMAT)
            val calendar = date?.toCalendar()
            calendar?.get(Calendar.HOUR_OF_DAY).defaultIfNull(0) * 60 + calendar?.get(Calendar.MINUTE).defaultIfNull(0)
        }
}

fun Execution.stopMachine(): Boolean {
    return this.machineStopped == true
}

fun Execution.stoppageReason(): Boolean {
    return this.stoppageReason == true
}

data class Execution(
    val id: Int,
    val siteId: Int,
    val siteExecutionId: Int,
    val positionId: Int,
    val ciltId: Int,
    val ciltSequenceId: Int,
    val levelId: Int?,
    val route: String?,
    val userId: Int,
    val userWhoExecutedId: Int,
    val specialWarning: String?,
    val secuenceSchedule: String,
    val allowExecuteBefore: Boolean,
    val allowExecuteBeforeMinutes: Int,
    val toleranceBeforeMinutes: Int,
    val toleranceAfterMinutes: Int,
    val allowExecuteAfterDue: Boolean,
    val secuenceStart: String?,
    val secuenceStop: String?,
    val duration: Int?,
    val realDuration: Int?,
    val standardOk: String,
    val initialParameter: String?,
    val evidenceAtCreation: Boolean,
    val finalParameter: String?,
    val evidenceAtFinal: Boolean,
    val nok: Boolean,
    val stoppageReason: Boolean?,
    val machineStopped: Boolean?,
    val amTagId: Int,
    val referencePoint: String?,
    val secuenceList: String,
    val secuenceColor: String,
    val ciltTypeId: Int,
    val ciltTypeName: String,
    val referenceOplSopId: Int,
    val remediationOplSopId: String,
    val toolsRequiered: String,
    val selectableWithoutProgramming: Boolean,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val evidences: List<CiltEvidence>,
    val referenceOplSop: OplSop?,
    val remediationOplSop: OplSop?,
)

fun Execution.getStatus(): String {
    val time = this.secuenceSchedule.parseUTCToLocal().toHourMinuteString()
    val currentTime = Calendar.getInstance().toHourMinuteString()
    val timeInMinutes = getMinutesDifference(currentTime, time)
    val minutesBefore = this.allowExecuteBeforeMinutes
    val minutesAfter = this.toleranceAfterMinutes

    if (this.status != "A") {
        return "Completada"
    }

    if (timeInMinutes >= minutesBefore) {
        return "Pendiente"
    }

    if ((timeInMinutes + minutesAfter) < 0) {
        return "Vencida"
    }

    if (abs(timeInMinutes) <= minutesAfter || timeInMinutes >= 0) {
        return "En tiempo"
    }
    return "Vencida"
}

fun Execution.getStatusColor(): Color {
    return when (this.getStatus()) {
        "Pendiente" -> return Color.Yellow
        "Completada" -> return Color.Blue
        "Vencida" -> return Color.Red
        "En tiempo" -> return Color.Green
        else -> Color.Blue
    }
}

fun Execution.getStatusTextColor(): Color {
    return when (this.getStatus()) {
        "Pendiente", "En tiempo" -> Color.Black
        else -> Color.White
    }
}

fun Execution.validate(context: Context): Pair<Int, String> {
    val time = this.secuenceSchedule.parseUTCToLocal().toHourMinuteString()
    val currentTime = Calendar.getInstance().toHourMinuteString()
    val scheduleDate = this.secuenceSchedule.parseUTCToLocal().toDate(format = NORMAL_FORMAT)
    val timeInMinutes = getMinutesDifference(currentTime, time)

    val timeBefore =
        scheduleDate?.toCalendar().apply {
            this?.add(Calendar.MINUTE, -allowExecuteBeforeMinutes)
        }?.time?.format()

    val timeAfter =
        scheduleDate?.toCalendar().apply {
            this?.add(Calendar.MINUTE, toleranceAfterMinutes)
        }?.time?.format()

    Log.e("test", "Time: $time")
    Log.e("test", "currentTime: $currentTime")
    Log.e("test", "scheduleDate: $scheduleDate")
    Log.e("test", "scheduleDate calendar: ${scheduleDate?.toCalendar()?.time}")
    Log.e("test", "timeInMinutes: $timeInMinutes")
    Log.e("test", "timeBefore: $timeBefore")
    Log.e("test", "timeAfter: $timeAfter")
    Log.e("test", "minutes toleranceBeforeMinutes: $allowExecuteBeforeMinutes")
    Log.e("test", "minutes toleranceAfterMinutes: $toleranceAfterMinutes")
    Log.e("test", "allowExecuteAfterDue: $allowExecuteAfterDue")

    if (scheduleDate?.toCalendar()?.before(Calendar.getInstance()).defaultIfNull(false)) {
        if (!this.allowExecuteAfterDue) {
            return Pair(0, context.getString(R.string.execution_one, scheduleDate))
        }
    }

    return if (timeInMinutes > 0) {
        if (this.allowExecuteBefore && this.allowExecuteBeforeMinutes >= timeInMinutes) {
            Pair(1, "execution_success")
        } else {
            Pair(0, context.getString(R.string.execution_three, timeBefore, scheduleDate?.format()))
        }
    } else {
        if (this.toleranceAfterMinutes >= abs(timeInMinutes)) {
            Pair(1, "execution_success")
        } else {
            Pair(0, context.getString(R.string.execution_two, scheduleDate?.format(), timeAfter))
        }
    }
}

fun Execution.isValidExecution(context: Context): Boolean {
    return validate(context).first == 1
}

data class CiltEvidence(
    val id: Int,
)

data class OplSop(
    val id: Int,
    val siteId: Int,
    val title: String,
    val objective: String,
    val creatorId: Int,
    val creatorName: String,
    val reviewerId: Int,
    val reviewerName: String,
    val oplType: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String,
)
