package com.ih.m2.domain.model

data class User(
    val name: String,
    val email: String,
    val token: String,
    val roles: List<String>,
    val logo: String
)