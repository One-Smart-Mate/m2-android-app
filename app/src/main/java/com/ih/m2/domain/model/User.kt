package com.ih.m2.domain.model

import com.ih.m2.data.database.entities.UserEntity

data class User(
    val name: String,
    val email: String,
    val token: String,
    val roles: List<String>,
    val logo: String,
    val companyId: String,
    val siteId: String,
    val companyName: String,
    val siteName: String
) {
    companion object {
        fun mockUser() = User(
            name = "testName",
            email = "test@gmail.com",
            token = "",
            roles = listOf("Admin","mechanic"),
            logo = "",
            companyId = "",
            siteId = "",
            companyName = "Company name",
            siteName = "site name"
        )
    }
}


fun User.toEntity(): UserEntity {
    return UserEntity(
        name = this.name,
        email = this.email,
        token = this.token,
        logo = this.logo,
        roles = this.roles.joinToString(separator = ","),
        companyId = this.companyId,
        siteId = this.siteId,
        companyName = this.companyName,
        siteName = this.siteName
    )
}