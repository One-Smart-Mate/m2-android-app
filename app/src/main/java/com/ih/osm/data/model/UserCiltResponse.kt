package com.ih.osm.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val data: UserCiltData,
)

data class UserCiltData(
    val userInfo: UserInfo,
    val positions: List<Position>,
)

data class UserInfo(
    @SerializedName("id") val id: String,
    val name: String,
    val email: String,
)

data class Position(
    @SerializedName("id") val id: Int,
    val name: String,
    val siteName: String,
    val areaName: String,
    @SerializedName("ciltMasters") val ciltMasters: List<CiltMaster>,
)

data class CiltMaster(
    @SerializedName("id") val id: Int,
    val siteId: Int,
    val positionId: Int,
    val ciltName: String,
    val ciltDescription: String,
    val creatorName: String,
    val reviewerName: String,
    val approvedByName: String,
    val urlImgLayout: String?,
    val updatedAt: String?,
    val status: String,
    val sequences: List<Sequence>,
)

data class Sequence(
    @SerializedName("id") val id: Int,
    val levelName: String,
    val ciltTypeName: String,
    @SerializedName("secuenceList") val secuenceList: String,
    @SerializedName("secuenceColor") val secuenceColor: String,
    val toolsRequired: String,
    val standardOk: String,
    val stoppageReason: Int,
    val machineStopped: Int,
    val referencePoint: String?,
    val status: String,
    @SerializedName("executions") val executions: List<Execution>,
)

data class Execution(
    @SerializedName("id") val id: Int,
    val secuenceStart: String,
    val secuenceStop: String,
    val duration: Int,
    val initialParameter: String,
    val finalParameter: String,
)
