package com.ih.m2.domain.model

import com.ih.m2.data.database.entities.UserEntity

data class User(
    val name: String,
    val email: String,
    val token: String,
    val roles: List<String>,
    val logo: String
)


fun User.toEntity(): UserEntity {
    return UserEntity(
        name = this.name,
        email = this.email,
        token = this.token,
        logo = this.logo,
        roles = this.roles.joinToString(separator = ",")
    )
}