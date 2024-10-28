package com.ih.osm.data.repository.auth

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.data.api.ApiService
import com.ih.osm.data.database.dao.UserDao
import com.ih.osm.data.database.entities.toDomain
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject
import org.json.JSONObject
import retrofit2.Response

class AuthRepositoryImpl
@Inject
constructor(
    private val apiService: ApiService,
    private val dao: UserDao
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

    override suspend fun save(user: User): Long {
        return dao.insertUser(user.toEntity())
    }

    override suspend fun get(): User? {
        return dao.getUser().toDomain()
    }

    override suspend fun logout(): Int {
        dao.getUser()?.let {
            return dao.deleteUser(it)
        }
        return 0
    }

    override suspend fun getSiteId(): String {
        return dao.getUser()?.siteId.orEmpty()
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
