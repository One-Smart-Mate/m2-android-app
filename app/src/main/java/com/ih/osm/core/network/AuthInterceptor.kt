package com.ih.osm.core.network

import com.ih.osm.data.database.dao.UserDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val dao: UserDao,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val user = runBlocking { dao.getUser() }
            val token = user?.token.orEmpty()
            val requestBuilder = chain.request().newBuilder()

            if (token.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            return chain.proceed(requestBuilder.build())
        }
    }
