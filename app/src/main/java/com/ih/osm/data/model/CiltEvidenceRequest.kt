package com.ih.osm.data.model

data class CiltEvidenceRequest(
    val executionId: Int,
    val evidenceUrl: String,
    val type: String,
    val createdAt: String,
)
