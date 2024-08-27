package com.osm.data.model

import com.osm.domain.model.Card

data class SolutionResponse(val data: Card, val status: Int, val message: String)

fun SolutionResponse.toDomain() = this.data