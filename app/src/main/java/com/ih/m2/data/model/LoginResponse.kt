package com.ih.m2.data.model

import com.ih.m2.domain.model.User

data class LoginResponse(val data: User, val status: Int, val message: String)

fun LoginResponse.toDomain(): User {
    return this.data
}