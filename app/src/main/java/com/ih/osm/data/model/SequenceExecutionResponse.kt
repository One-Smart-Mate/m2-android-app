package com.ih.osm.data.model

import com.ih.osm.domain.model.SequenceExecutionData

data class SequenceExecutionResponse(
    val data: SequenceExecutionData,
)

fun SequenceExecutionResponse.toDomain() = this.data
