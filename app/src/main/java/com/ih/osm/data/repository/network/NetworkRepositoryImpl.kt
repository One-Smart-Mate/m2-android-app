package com.ih.osm.data.repository.network

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.FastLoginRequest
import com.ih.osm.data.model.GenerateCiltExecutionRequest
import com.ih.osm.data.model.GenerateCiltExecutionResponse
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.LogoutRequest
import com.ih.osm.data.model.RefreshTokenRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.SendFastPasswordRequest
import com.ih.osm.data.model.SendFastPasswordResponse
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.model.toDomainModel
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Catalogs
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.LevelStats
import com.ih.osm.domain.model.LevelTreeData
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.SequenceExecution
import com.ih.osm.domain.repository.network.NetworkRepository
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NetworkRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
    ) : NetworkRepository {
        override suspend fun login(data: LoginRequest): LoginResponse {
            val response = apiService.login(data).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!
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

        override suspend fun getRemoteCardTypes(siteId: String): List<CardType> {
            val response = apiService.getCardTypes(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteEmployees(siteId: String): List<Employee> {
            val response = apiService.getEmployees(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteEmployeesByRole(
            siteId: String,
            roleName: String,
        ): List<Employee> {
            val response = apiService.getEmployeesByRole(siteId, roleName).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteLevels(siteId: String): List<Level> {
            val response = apiService.getLevels(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemotePreclassifiers(siteId: String): List<Preclassifier> {
            val response = apiService.getPreclassifiers(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemotePriorities(siteId: String): List<Priority> {
            val response = apiService.getPriorities(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCardsByUser(siteId: String): List<Card> {
            val response = apiService.getCards(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCardsByUser(
            siteId: String,
            page: Int?,
            limit: Int?,
        ): List<Card> {
            val response = apiService.getCards(siteId, page, limit).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCardDetail(cardId: String): Card? {
            val response = apiService.getCardDetail(cardId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun saveRemoteCard(card: CreateCardRequest): Card {
            val response = apiService.createCard(card).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCardsZone(
            superiorId: String,
            siteId: String,
        ): List<Card> {
            val response = apiService.getCardsZone(superiorId, siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun saveRemoteDefinitiveSolution(createDefinitiveSolutionRequest: CreateDefinitiveSolutionRequest): Card {
            val response = apiService.saveDefinitiveSolution(createDefinitiveSolutionRequest).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun saveRemoteProvisionalSolution(createProvisionalSolutionRequest: CreateProvisionalSolutionRequest): Card {
            val response =
                apiService.saveProvisionalSolution(createProvisionalSolutionRequest).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCardsLevelMachine(
            levelMachine: String,
            siteId: String,
        ): List<Card> {
            val response = apiService.getCardsLevelMachine(siteId, levelMachine).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun updateRemoteMechanic(body: UpdateMechanicRequest) {
            val response = apiService.updateMechanic(body).execute()
            if (!response.isSuccessful && response.body() == null) {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteOplsByLevel(levelId: String): List<Opl> {
            val response = apiService.getOplsByLevel(levelId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        private fun <T> Response<T>.getErrorMessage(): String {
            val instance = FirebaseCrashlytics.getInstance()
            try {
                val data =
                    JSONObject(
                        this
                            .errorBody()
                            ?.charStream()
                            ?.readText()
                            .orEmpty(),
                    )
                val message = data.getString("message")
                instance.setCustomKey("Custom_Error_API_Service ", message)
                instance.log(message)
                instance.recordException(Exception(data.toString()))
                LoggerHelperManager.logException(Exception(data.toString()))
                return message
            } catch (e: Exception) {
                instance.recordException(e)
                LoggerHelperManager.logException(e)
                return e.localizedMessage.orEmpty()
            }
        }

        override suspend fun logout(body: LogoutRequest) {
            try {
                apiService.logout(body)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                LoggerHelperManager.logException(e)
                error(e.localizedMessage.orEmpty())
            }
        }

        override suspend fun getCilts(
            userId: String,
            date: String,
        ): CiltData {
            val response = apiService.getCilts().execute()
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody?.data != null) {
                responseBody.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getOplById(id: String): Opl {
            val response = apiService.getOplById(id).execute()
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody?.data != null) {
                responseBody.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun startSequenceExecution(body: StartSequenceExecutionRequest): SequenceExecution {
            val response = apiService.startSequenceExecution(body).execute()
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody?.data != null) {
                responseBody.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun stopSequenceExecution(body: StopSequenceExecutionRequest): SequenceExecution {
            val response = apiService.stopSequenceExecution(body).execute()
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody?.data != null) {
                responseBody.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun createEvidence(body: CiltEvidenceRequest): CiltSequenceEvidence {
            val response = apiService.createEvidence(body).execute()
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody?.data != null) {
                responseBody.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun fastLogin(body: FastLoginRequest): LoginResponse {
            val response = apiService.fastLogin(body).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getSequence(id: Int): Sequence {
            val response = apiService.getSequence(id).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun sendFastPassword(body: SendFastPasswordRequest): SendFastPasswordResponse {
            val response = apiService.sendFastPassword(body).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCiltProcedureByLevel(levelId: String): CiltProcedureData {
            val response = apiService.getCiltProcedureByLevel(levelId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun createCiltExecution(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse {
            val response = apiService.createCiltExecution(request).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun generateCiltExecution(request: GenerateCiltExecutionRequest): GenerateCiltExecutionResponse {
            val response = apiService.generateCiltExecution(request).execute()

            return if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!

                responseBody
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun refreshToken(body: RefreshTokenRequest): LoginResponse {
            val response = apiService.refreshToken(body).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getCatalogsBySite(siteId: String): Catalogs {
            val response = apiService.getCatalogsBySite(siteId).execute()
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody?.data != null) {
                responseBody.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteCardsByLevel(
            levelId: String,
            siteId: String,
            page: Int?,
            limit: Int?,
        ): List<Card> {
            val response = apiService.getCardsByLevel(levelId, siteId, page, limit).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteLevelsWithLocation(
            siteId: String,
            page: Int?,
            limit: Int?,
        ): List<Level> {
            val response = apiService.getLevelsWithLocation(siteId, page, limit).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteSiteLevels(
            siteId: String,
            page: Int?,
            limit: Int?,
        ): List<Level> {
            val response = apiService.getSiteLevels(siteId, page, limit).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteLevelTreeLazy(
            siteId: String,
            page: Int?,
            limit: Int?,
            depth: Int?,
        ): LevelTreeData {
            val response = apiService.getLevelTreeLazy(siteId, page, limit, depth).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.data.toDomainModel()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteChildrenLevels(
            siteId: String,
            parentId: String,
            page: Int?,
            limit: Int?,
        ): List<Level> {
            val response = apiService.getChildrenLevels(siteId, parentId, page, limit).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun getRemoteLevelStats(
            siteId: String,
            page: Int?,
            limit: Int?,
        ): LevelStats {
            val response = apiService.getLevelStats(siteId, page, limit).execute()
            return if (response.isSuccessful && response.body() != null) {
                response
                    .body()!!
                    .data.stats
                    .toDomainModel()
            } else {
                error(response.getErrorMessage())
            }
        }

        override suspend fun findLevelByMachineId(
            siteId: String,
            machineId: String,
        ): List<Level> {
            val response = apiService.findLevelByMachineId(siteId, machineId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }
    }
