package com.ih.osm.data.model

data class StopSequenceExecutionRequest(
    val id: Int,
    val stopDate: String,
    val initialParameter: String,
    val evidenceAtCreation: Boolean,
    val finalParameter: String,
    val evidenceAtFinal: Boolean,
    val nok: Boolean,
    val amTagId: Int,
)
