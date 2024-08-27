package com.osm.domain.repository.auth

import com.osm.data.model.LoginRequest
import com.osm.data.model.RestorePasswordRequest
import com.osm.data.model.UpdateTokenRequest
import com.osm.domain.model.User

interface AuthRepository {
    suspend fun login(data: LoginRequest): User
    suspend fun sendRestorePasswordCode(data: RestorePasswordRequest)
    suspend fun verifyPasswordCode(data: RestorePasswordRequest)
    suspend fun resetPassword(data: RestorePasswordRequest)
    suspend fun updateToken(data: UpdateTokenRequest)
}