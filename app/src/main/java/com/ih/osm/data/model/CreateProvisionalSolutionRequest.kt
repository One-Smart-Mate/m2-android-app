package com.ih.osm.data.model

data class CreateProvisionalSolutionRequest(
    val cardId: Int,
    val userProvisionalSolutionId: Int,
    val userAppProvisionalSolutionId: Int,
    val comments: String,
    val evidences: List<CreateEvidenceRequest>
)
