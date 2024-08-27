package com.osm.data.model

data class UpdateTokenRequest(
    val userId: Int,
    val appToken: String
)