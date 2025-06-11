package com.ih.osm.data.model

data class UpdateCiltEvidenceRequest(
    val id: Int,
    val siteId: Int,
    val positionId: Int,
    val ciltId: Int,
    val ciltExecutionsEvidencesId: Int,
    val evidenceUrl: String,
)
