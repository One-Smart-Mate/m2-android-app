package com.ih.osm.data.model

import com.ih.osm.domain.model.SequenceExecution

data class StopSequenceExecutionResponse(
    val data: SequenceExecution,
    val status: Int,
    val message: String,
)

fun StopSequenceExecutionResponse.toDomain() = this.data
