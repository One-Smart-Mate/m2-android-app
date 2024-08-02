package com.ih.m2.domain.repository.auth

import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.RestorePasswordRequest
import com.ih.m2.data.model.UpdateTokenRequest
import com.ih.m2.domain.model.User

interface AuthRepository {
    suspend fun login(data: LoginRequest): User
    suspend fun sendRestorePasswordCode(data: RestorePasswordRequest)
    suspend fun verifyPasswordCode(data: RestorePasswordRequest)
    suspend fun resetPassword(data: RestorePasswordRequest)
    suspend fun updateToken(data: UpdateTokenRequest)
}