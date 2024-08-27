package com.osm.data.model

import com.osm.domain.model.Card

data class GetCardsResponse(val data: List<Card>,  val status: Int, val message: String)

fun GetCardsResponse.toDomain() = this.data