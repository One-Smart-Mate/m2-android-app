package com.osm.data.repository.auth

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.osm.data.api.ApiService
import com.osm.data.model.LoginRequest
import com.osm.data.model.RestorePasswordRequest
import com.osm.data.model.UpdateTokenRequest
import com.osm.data.model.toDomain
import com.osm.domain.model.User
import com.osm.domain.repository.auth.AuthRepository
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    override suspend fun login(data: LoginRequest): User {
        val response = apiService.login(data).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }

    override suspend fun sendRestorePasswordCode(data: RestorePasswordRequest) {
        val response = apiService.sendRestorePasswordCode(data).execute()
        if (!response.isSuccessful || response.body() == null) {
            error(response.getErrorMessage())
        }
    }

    override suspend fun verifyPasswordCode(data: RestorePasswordRequest) {
        val response = apiService.verifyPasswordCode(data).execute()
        if (!response.isSuccessful || response.body() == null) {
            error(response.getErrorMessage())
        }
    }

    override suspend fun resetPassword(data: RestorePasswordRequest) {
        val response = apiService.resetPassword(data).execute()
        if (!response.isSuccessful || response.body() == null) {
            error(response.getErrorMessage())
        }
    }

    override suspend fun updateToken(data: UpdateTokenRequest) {
        val response = apiService.updateToken(data).execute()
        if (!response.isSuccessful || response.body() == null) {
            error(response.getErrorMessage())
        }
    }
}

fun <T> Response<T>.getErrorMessage(): String {
    val instance = FirebaseCrashlytics.getInstance()
    try {
        val data = JSONObject(this.errorBody()?.charStream()?.readText().orEmpty())
        val message = data.getString("message")
        instance.setCustomKey("Custom_Error_API_Service ", message)
        instance.log(message)
        instance.recordException(Exception(data.toString()))
        return message
    } catch (e: Exception) {
        instance.recordException(e)
        return e.localizedMessage.orEmpty()
    }
}