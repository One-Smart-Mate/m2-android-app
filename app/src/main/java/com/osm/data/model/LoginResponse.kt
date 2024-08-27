package com.osm.data.model

import com.osm.domain.model.User

data class LoginResponse(val data: User, val status: Int, val message: String)

fun LoginResponse.toDomain(): User {
    return this.data
}