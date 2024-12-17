package com.ih.osm.data.model

import com.ih.osm.domain.model.User

data class LoginResponse(val data: UserWrapper, val status: Int, val message: String)

fun LoginResponse.toDomain(): User {
    val site = this.data.sites.firstOrNull()
    return User(
        userId = this.data.userId,
        name = this.data.name,
        email = this.data.email,
        token = this.data.token,
        roles = this.data.roles,
        companyName = this.data.companyName,
        companyId = this.data.companyId,
        siteId = site?.id.orEmpty(),
        siteName = site?.name.orEmpty(),
        logo = site?.logo.orEmpty(),
    )
}

data class UserWrapper(
    val userId: String,
    val name: String,
    val email: String,
    val token: String,
    val roles: List<String>,
    val logo: String,
    val companyId: String,
    val companyName: String,
    val sites: List<Site>,
)

data class Site(
    val id: String,
    val name: String,
    val logo: String,
)
