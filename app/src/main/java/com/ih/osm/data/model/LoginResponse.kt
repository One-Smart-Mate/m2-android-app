package com.ih.osm.data.model

import com.ih.osm.domain.model.User

data class LoginResponse(val data: User, val status: Int, val message: String)

fun LoginResponse.toDomain(): User {
    return this.data
}