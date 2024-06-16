package com.ih.m2.domain.repository

import com.ih.m2.data.model.LoginRequest
import com.ih.m2.domain.model.User

interface AuthRepository {
    suspend fun login(data: LoginRequest): User
}