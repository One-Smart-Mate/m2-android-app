package com.ih.m2.data.model

data class UpdateTokenRequest(
    val userId: Int,
    val appToken: String
)