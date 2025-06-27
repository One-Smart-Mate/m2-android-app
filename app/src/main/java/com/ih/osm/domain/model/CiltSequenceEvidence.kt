package com.ih.osm.domain.model

data class CiltSequenceEvidence(
    val id: Int,
    val siteId: Int,
    val positionId: Int,
    val ciltId: Int,
    val ciltSequencesExecutionsId: Int,
    val evidenceUrl: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
)
