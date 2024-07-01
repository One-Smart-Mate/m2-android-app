package com.ih.m2.data.repository.auth

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.toDomain
import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.auth.AuthRepository
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