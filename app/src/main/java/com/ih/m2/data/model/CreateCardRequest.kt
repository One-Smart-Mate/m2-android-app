package com.ih.m2.data.model

data class CreateCardRequest(
    val siteId: Int,
    val cardUUID: String,
    val feasibility: String,
    val effect: String,
    val cardCreationDate: String,
    val areaId: Int,
    val priorityId: Int,
    val cardTypeValue: String,
    val cardTypeId: Int,
    val preclassifierId: Int,
    val creatorId: Int,
    val responsableId: Int,
    val mechanicId: Int,
    val userProvisionalSolutionId: Int,
    val userAppProvisionalSolutionId: Int,
    val userDefinitiveSolutionId: Int,
    val userAppDefinitiveSolutionId: Int,
    val managerId: Int,
    val commentsAtCardCreation: String,
    val evidenceAucr: Int,
    val evidenceVicr: Int,
    val evidenceImcr: Int,
    val evidenceAucl: Int,
    val evidenceVicl: Int,
    val evidenceImcl: Int
)