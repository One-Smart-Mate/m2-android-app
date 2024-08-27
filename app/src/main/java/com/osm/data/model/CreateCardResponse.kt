package com.osm.data.model

import com.osm.domain.model.Card

data class CreateCardResponse(val data: Card, val status: Int, val message: String)

fun CreateCardResponse.toDomain() = this.data