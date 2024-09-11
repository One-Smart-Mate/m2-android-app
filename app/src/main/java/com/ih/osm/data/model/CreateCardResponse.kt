package com.ih.osm.data.model

import com.ih.osm.domain.model.Card

data class CreateCardResponse(val data: Card, val status: Int, val message: String)

fun CreateCardResponse.toDomain() = this.data