package com.ih.osm.domain.model

import com.ih.osm.data.database.entities.SessionEntity

data class Session(
    val userId: String,
    val name: String,
    val email: String,
    val roles: List<String>,
    val logo: String,
    val companyId: String,
    val siteId: String,
    val companyName: String,
    val siteName: String,
) {
    companion object {
        fun mockUser() =
            Session(
                userId = "",
                name = "testName",
                email = "test@gmail.com",
                roles = listOf("Admin", "mechanic"),
                logo = "",
                companyId = "",
                siteId = "",
                companyName = "Universal S.A de C.V",
                siteName = "site name",
            )
    }
}

fun Session.toEntity(): SessionEntity =
    SessionEntity(
        userId = this.userId,
        name = this.name,
        email = this.email,
        logo = this.logo,
        roles = this.roles.joinToString(separator = ","),
        companyId = this.companyId,
        siteId = this.siteId,
        companyName = this.companyName,
        siteName = this.siteName,
    )
