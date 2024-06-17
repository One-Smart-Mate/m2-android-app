package com.ih.m2.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.m2.domain.model.User

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "token")
    val token: String,
    @ColumnInfo(name = "logo")
    val logo: String,
    @ColumnInfo(name = "roles")
    val roles: String
)

fun UserEntity?.toDomain(): User? {
    if (this != null) {
        return User(
            name = this.name,
            email = this.email,
            token = this.token,
            logo = this.logo,
            roles = this.roles.split(",")
        )
    }
    return null
}