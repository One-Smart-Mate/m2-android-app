package com.ih.osm.data.model

data class CiltEvidenceRequest(
    val siteId: Int,
    val positionId: Int,
    val ciltId: Int,
    val ciltExecutionsEvidencesId: Int,
    val evidenceUrl: String,
    val createdAt: String,
)
