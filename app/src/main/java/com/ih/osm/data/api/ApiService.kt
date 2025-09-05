package com.ih.osm.data.api

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.CiltEvidenceResponse
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateCardResponse
import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.FastLoginRequest
import com.ih.osm.data.model.GenerateCiltExecutionRequest
import com.ih.osm.data.model.GenerateCiltExecutionResponse
import com.ih.osm.data.model.GetCardDetailResponse
import com.ih.osm.data.model.GetCardTypesResponse
import com.ih.osm.data.model.GetCardsResponse
import com.ih.osm.data.model.GetCiltResponse
import com.ih.osm.data.model.GetEmployeesResponse
import com.ih.osm.data.model.GetLevelsResponse
import com.ih.osm.data.model.GetOplByIdResponse
import com.ih.osm.data.model.GetOplsResponse
import com.ih.osm.data.model.GetPreclassifiersResponse
import com.ih.osm.data.model.GetPrioritiesResponse
import com.ih.osm.data.model.GetProcedimientoCiltResponse
import com.ih.osm.data.model.GetSequenceResponse
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.LogoutRequest
import com.ih.osm.data.model.RefreshTokenRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.SendFastPasswordRequest
import com.ih.osm.data.model.SendFastPasswordResponse
import com.ih.osm.data.model.SolutionResponse
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StartSequenceExecutionResponse
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionResponse
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

    @POST("users/logout")
    suspend fun logout(
        @Body body: LogoutRequest,
    )

    @GET("users/site/{siteId}/role/{roleName}")
    fun getEmployeesByRole(
        @Path("siteId") siteId: String,
        @Path("roleName") roleName: String,
    ): Call<GetEmployeesResponse>

    @GET("cilt-sequences-executions/of-day")
    fun getCilts(): Call<GetCiltResponse>

    @GET("/opl-mstr/{id}")
    fun getOplById(
        @Path("id") id: String,
    ): Call<GetOplByIdResponse>

    @PUT("/cilt-sequences-executions/start")
    fun startSequenceExecution(
        @Body body: StartSequenceExecutionRequest,
    ): Call<StartSequenceExecutionResponse>

    @PUT("/cilt-sequences-executions/stop")
    fun stopSequenceExecution(
        @Body body: StopSequenceExecutionRequest,
    ): Call<StopSequenceExecutionResponse>

    @POST("cilt-sequences-executions/evidence/create")
    fun createEvidence(
        @Body body: CiltEvidenceRequest,
    ): Call<CiltEvidenceResponse>

    @GET("opl-levels/level/{levelId}")
    fun getOplsByLevel(
        @Path("levelId") levelId: String,
    ): Call<GetOplsResponse>

    @POST("auth/login-fast")
    fun fastLogin(
        @Body body: FastLoginRequest,
    ): Call<LoginResponse>

    @GET("cilt-sequences/{id}")
    fun getSequence(
        @Path("id") id: Int,
    ): Call<GetSequenceResponse>

    @POST("auth/send-fastpassword-by-phone")
    fun sendFastPassword(
        @Body body: SendFastPasswordRequest,
    ): Call<SendFastPasswordResponse>

    @GET("cilt-mstr-position-levels/level/{levelId}/recent-executions")
    fun getProcedimientoCiltsByLevel(
        @Path("levelId") levelId: String,
    ): Call<GetProcedimientoCiltResponse>

    @POST("cilt-sequences-executions/create")
    fun createCiltExecution(
        @Body body: CreateCiltExecutionRequest,
    ): Call<CreateCiltExecutionResponse>

    @POST("cilt-sequences-executions/generate")
    fun generateCiltExecution(
        @Body body: GenerateCiltExecutionRequest,
    ): Call<GenerateCiltExecutionResponse>

    @POST("auth/refresh-token")
    fun refreshToken(
        @Body body: RefreshTokenRequest,
    ): Call<LoginResponse>
}
