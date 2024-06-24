package com.ih.m2.data.model

data class CreateCardRequest(
    val siteId: Int,
    val cardUUID: String,
    val cardCreationDate: String,
    val areaId: Int,
    val priorityId: Int,
    val cardTypeValue: String,
    val cardTypeId: Int,
    val preclassifierId: Int,
    val creatorId: Int,
    val comments: String,
    val evidences: List<CreateEvidenceRequest>
)