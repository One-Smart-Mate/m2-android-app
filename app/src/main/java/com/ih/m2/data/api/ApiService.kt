package com.ih.m2.data.api


import com.ih.m2.data.model.CreateCardRequest
import com.ih.m2.data.model.CreateCardResponse
import com.ih.m2.data.model.CreateDefinitiveSolutionRequest
import com.ih.m2.data.model.GetCardDetailResponse
import com.ih.m2.data.model.GetCardTypesResponse
import com.ih.m2.data.model.GetCardsResponse
import com.ih.m2.data.model.GetEmployeesResponse
import com.ih.m2.data.model.GetLevelsResponse
import com.ih.m2.data.model.GetPreclassifiersResponse
import com.ih.m2.data.model.GetPrioritiesResponse
import com.ih.m2.data.model.LoginRequest
import com.ih.m2.data.model.LoginResponse
import com.ih.m2.data.model.SolutionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService  {

    @POST("auth/login")
    fun login(
        @Body body: LoginRequest
    ) : Call<LoginResponse>

    @GET("card/all/{siteId}")
    fun getCards(
        @Path("siteId") sitId: String
    ): Call<GetCardsResponse>


    @GET("card-types/all/{siteId}")
    fun getCardTypes(
        @Path("siteId") sitId: String
    ): Call<GetCardTypesResponse>

    @GET("preclassifier/site/{siteId}")
    fun getPreclassifiers(
        @Path("siteId") siteId: String
    ): Call<GetPreclassifiersResponse>

    @GET("priority/all/{siteId}")
    fun getPriorities(
        @Path("siteId") siteId: String
    ): Call<GetPrioritiesResponse>


    @GET("card/{cardId}")
    fun getCardDetail(
        @Path("cardId") cardId: String
    ): Call<GetCardDetailResponse>


    @POST("card/create")
    fun createCard(
        @Body body: CreateCardRequest
    ) : Call<CreateCardResponse>


    @GET("level/all/{siteId}")
    fun getLevels(
        @Path("siteId") siteId: String
    ) : Call<GetLevelsResponse>

    @GET("users/all/{siteId}")
    fun getEmployees(
        @Path("siteId") siteId: String
    ) : Call<GetEmployeesResponse>

    @GET("card/all/zone/{siteId}")
    fun getCardsZone(
        @Path("siteId") sitId: String
    ): Call<GetCardsResponse>

    @PUT("card/update/definitive-solution")
    fun saveDefinitiveSolution(
        @Body body: CreateDefinitiveSolutionRequest
    ) : Call<SolutionResponse>

}

