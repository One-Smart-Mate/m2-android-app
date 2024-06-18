package com.ih.m2.data.api


import com.ih.m2.data.model.GetCardsResponse
import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService  {

    @POST("auth/login")
    fun login(
        @Body body: LoginRequest
    ) : Call<LoginResponse>

    @GET("card/all/{id}")
    fun getCards(
        @Path("id") sitId: String
    ): Call<GetCardsResponse>

}

