package com.ih.m2.data.api


import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService  {

    @POST("auth/login")
    fun login(
        @Body body: LoginRequest
    ) : Call<LoginResponse>

}

