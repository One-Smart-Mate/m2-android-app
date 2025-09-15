package com.ih.osm.data.model

import com.ih.osm.domain.model.Card

data class GetCardsResponse(
    val data: List<Card>,
    val status: Int,
    val message: String,
)

fun GetCardsResponse.toDomain() = this.data
