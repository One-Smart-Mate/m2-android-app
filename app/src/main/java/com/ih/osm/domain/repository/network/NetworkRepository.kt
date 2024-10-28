package com.ih.osm.domain.repository.network

import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.User

interface NetworkRepository {
    suspend fun login(data: LoginRequest): User

    suspend fun sendRestorePasswordCode(data: RestorePasswordRequest)

    suspend fun verifyPasswordCode(data: RestorePasswordRequest)

    suspend fun resetPassword(data: RestorePasswordRequest)

    suspend fun updateToken(data: UpdateTokenRequest)

    suspend fun getRemoteCardTypes(siteId: String): List<CardType>
}
