package com.ih.osm.data.model

import com.ih.osm.domain.model.Card

data class SolutionResponse(
    val data: Card,
    val status: Int,
    val message: String,
)

fun SolutionResponse.toDomain() = this.data
