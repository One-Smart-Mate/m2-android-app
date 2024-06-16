package com.ih.m2.data.repository

import android.util.Log
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.toDomain
import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    override suspend fun login(data: LoginRequest): User {
        val response = apiService.login(data).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.data
        } else {
            error(response.errorBody().toString())
        }
    }
}