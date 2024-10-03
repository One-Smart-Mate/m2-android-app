package com.ih.osm.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.osm.domain.model.User

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "token")
    val token: String,
    @ColumnInfo(name = "logo")
    val logo: String,
    @ColumnInfo(name = "roles")
    val roles: String,
    @ColumnInfo("company_id")
    val companyId: String,
    @ColumnInfo("site_id")
    val siteId: String,
    @ColumnInfo("company_name")
    val companyName: String,
    @ColumnInfo("site_name")
    val siteName: String
)

fun com.ih.osm.data.database.entities.UserEntity?.toDomain(): User? {
    if (this != null) {
        return User(
            userId = this.userId,
            name = this.name,
            email = this.email,
            token = this.token,
            logo = this.logo,
            roles = this.roles.split(","),
            companyId = this.companyId,
            siteId = this.siteId,
            companyName = this.companyName,
            siteName = this.siteName
        )
    }
    return null
}
