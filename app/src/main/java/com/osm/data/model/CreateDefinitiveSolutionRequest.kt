package com.osm.data.model

data class CreateDefinitiveSolutionRequest(
    val cardId: Int,
    val userDefinitiveSolutionId: Int,
    val userAppDefinitiveSolutionId: Int,
    val comments: String,
    val evidences: List<CreateEvidenceRequest>
)