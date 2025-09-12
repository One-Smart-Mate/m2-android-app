package com.ih.osm.core.network

import com.ih.osm.data.database.dao.UserDao
import com.ih.osm.data.model.RefreshTokenRequest
import com.ih.osm.domain.usecase.login.RefreshTokenUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator
    @Inject
    constructor(
        private val userDao: UserDao,
        private val refreshTokenUseCaseProvider: Provider<RefreshTokenUseCase>,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (responseCount(response) >= 2) return null

            val user = runBlocking { userDao.getUser() } ?: return null
            val oldToken = user.token ?: return null

            val newToken =
                runBlocking {
                    try {
                        val refreshTokenUseCase = refreshTokenUseCaseProvider.get()
                        val result = refreshTokenUseCase(RefreshTokenRequest(oldToken))
                        result.data.token
                    } catch (e: Exception) {
                        null
                    }
                } ?: return null

            runBlocking {
                userDao.updateToken(user.userId, newToken)
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        }

        private fun responseCount(response: Response): Int {
            var count = 1
            var prior = response.priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }
    }
