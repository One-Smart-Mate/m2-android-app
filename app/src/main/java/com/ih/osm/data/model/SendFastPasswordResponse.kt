package com.ih.osm.data.model

import com.ih.osm.domain.model.FastPassword

data class SendFastPasswordResponse(
    val data: FastPassword,
    val status: Int,
    val message: String,
)

fun SendFastPasswordResponse.toDomain() = this.data
