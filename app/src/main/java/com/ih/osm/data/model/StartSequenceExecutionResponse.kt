package com.ih.osm.data.model

import com.ih.osm.domain.model.SequenceExecution

data class StartSequenceExecutionResponse(
    val data: SequenceExecution,
    val status: Int,
    val message: String,
)

fun StartSequenceExecutionResponse.toDomain() = this.data
