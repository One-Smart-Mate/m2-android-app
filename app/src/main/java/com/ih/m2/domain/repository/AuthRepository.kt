package com.ih.m2.domain.repository

import com.ih.m2.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): User
}