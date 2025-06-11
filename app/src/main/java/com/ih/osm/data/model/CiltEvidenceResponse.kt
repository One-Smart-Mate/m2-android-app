package com.ih.osm.data.model

import com.ih.osm.domain.model.CiltSequenceEvidence

data class CiltEvidenceResponse(
    val data: CiltSequenceEvidence,
    val status: Int,
    val message: String,
)

fun CiltEvidenceResponse.toDomain() = this.data
