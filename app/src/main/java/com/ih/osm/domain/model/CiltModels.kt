package com.ih.osm.domain.model

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

fun Execution.stopMachine(): Boolean {
    return this.machineStopped == true
}

fun Execution.stoppageReason(): Boolean {
    return this.stoppageReason == true
}

data class Execution(
    val id: Int,
    val siteId: Int,
    val positionId: Int,
    val ciltId: Int,
    val ciltSequenceId: Int,
    val levelId: Int?,
    val route: String?,
    val userId: Int,
    val userWhoExecutedId: Int,
    val secuenceSchedule: String,
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
