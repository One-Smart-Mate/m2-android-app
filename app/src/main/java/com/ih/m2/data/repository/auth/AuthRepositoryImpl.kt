package com.ih.m2.data.repository.auth

import android.util.Log
import com.google.gson.Gson
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.toDomain
import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.auth.AuthRepository
import okhttp3.ResponseBody
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
    try {
        val data = JSONObject(this.errorBody()?.charStream()?.readText().orEmpty())
        return data.getString("message")
    } catch (e: Exception) {
        return e.localizedMessage.orEmpty()
    }
}