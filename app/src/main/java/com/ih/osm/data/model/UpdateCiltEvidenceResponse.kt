package com.ih.osm.data.model

import com.ih.osm.domain.model.CiltSequenceEvidence

data class UpdateCiltEvidenceResponse(
    val data: CiltSequenceEvidence,
    val status: Int,
    val message: String,
)

fun UpdateCiltEvidenceResponse.toDomain() = this.data
