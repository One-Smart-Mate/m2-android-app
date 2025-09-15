package com.ih.osm.data.model

data class LoginRequest(
    val email: String,
    val password: String,
    val timezone: String,
    val platform: String,
)
