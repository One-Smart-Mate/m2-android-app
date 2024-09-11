package com.ih.osm.data.model

data class UpdateTokenRequest(
    val userId: Int,
    val appToken: String
)