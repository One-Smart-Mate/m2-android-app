package com.ih.osm.data.model

data class FastLoginRequest(
    val fastPassword: String,
    val timezone: String,
    val platform: String,
)
