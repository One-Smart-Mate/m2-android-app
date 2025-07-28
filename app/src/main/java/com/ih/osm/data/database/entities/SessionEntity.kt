package com.ih.osm.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.osm.domain.model.Session

@Entity(tableName = "session_table")
data class SessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "email")
    val email: String,
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
    val siteName: String,
)

fun SessionEntity?.toDomain(): Session? {
    if (this != null) {
        return Session(
            userId = this.userId,
            name = this.name,
            email = this.email,
            logo = this.logo,
            roles = this.roles.split(","),
            companyId = this.companyId,
            siteId = this.siteId,
            companyName = this.companyName,
            siteName = this.siteName,
        )
    }
    return null
}
