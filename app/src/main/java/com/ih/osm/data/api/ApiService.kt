package com.ih.osm.data.api

import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateCardResponse
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.GetCardDetailResponse
import com.ih.osm.data.model.GetCardTypesResponse
import com.ih.osm.data.model.GetCardsResponse
import com.ih.osm.data.model.GetEmployeesResponse
import com.ih.osm.data.model.GetLevelsResponse
import com.ih.osm.data.model.GetPreclassifiersResponse
import com.ih.osm.data.model.GetPrioritiesResponse
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.SolutionResponse
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.data.model.UpdateTokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    fun login(
        @Body body: LoginRequest,
    ): Call<LoginResponse>

    @GET("card/all/{siteId}")
    fun getCards(
        @Path("siteId") sitId: String,
    ): Call<GetCardsResponse>

    @GET("card-types/all/{siteId}")
    fun getCardTypes(
        @Path("siteId") sitId: String,
    ): Call<GetCardTypesResponse>

    @GET("preclassifier/site/{siteId}")
    fun getPreclassifiers(
        @Path("siteId") siteId: String,
    ): Call<GetPreclassifiersResponse>

    @GET("priority/all/{siteId}")
    fun getPriorities(
        @Path("siteId") siteId: String,
    ): Call<GetPrioritiesResponse>

    @GET("card/{cardId}")
    fun getCardDetail(
        @Path("cardId") cardId: String,
    ): Call<GetCardDetailResponse>

    @POST("card/create")
    fun createCard(
        @Body body: CreateCardRequest,
    ): Call<CreateCardResponse>

    @GET("level/all/{siteId}")
    fun getLevels(
        @Path("siteId") siteId: String,
    ): Call<GetLevelsResponse>

    @GET("users/all/{siteId}")
    fun getEmployees(
        @Path("siteId") siteId: String,
    ): Call<GetEmployeesResponse>

    @GET("card/all/zone/{superiorId}/{siteId}")
    fun getCardsZone(
        @Path("superiorId") superiorId: String,
        @Path("siteId") sitId: String,
    ): Call<GetCardsResponse>

    @PUT("card/update/definitive-solution")
    fun saveDefinitiveSolution(
        @Body body: CreateDefinitiveSolutionRequest,
    ): Call<SolutionResponse>

    @PUT("card/update/provisional-solution")
    fun saveProvisionalSolution(
        @Body body: CreateProvisionalSolutionRequest,
    ): Call<SolutionResponse>

    @POST("users/send-code")
    fun sendRestorePasswordCode(
        @Body body: RestorePasswordRequest,
    ): Call<Any>

    @POST("users/verify-code")
    fun verifyPasswordCode(
        @Body body: RestorePasswordRequest,
    ): Call<Any>

    @POST("users/reset-password")
    fun resetPassword(
        @Body body: RestorePasswordRequest,
    ): Call<Any>

    @POST("users/app-token")
    fun updateToken(
        @Body body: UpdateTokenRequest,
    ): Call<Any>

    @GET("card/all/level-machine/{siteId}/{levelMachine}")
    fun getCardsLevelMachine(
        @Path("siteId") siteId: String,
        @Path("levelMachine") levelMachine: String,
    ): Call<GetCardsResponse>

    @POST("card/update/mechanic")
    fun updateMechanic(
        @Body body: UpdateMechanicRequest,
    ): Call<Any>

    @POST("users/logout/{userId}")
    suspend fun logout(
        @Path("userId") userId: Int,
    )
}
