package com.ih.osm.data.repository.network

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.file.FileHelper
import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.User
import com.ih.osm.domain.repository.network.NetworkRepository
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NetworkRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
        private val fileHelper: FileHelper,
    ) : NetworkRepository {
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

        private fun <T> Response<T>.getErrorMessage(): String {
            val instance = FirebaseCrashlytics.getInstance()
            try {
                val data = JSONObject(this.errorBody()?.charStream()?.readText().orEmpty())
                val message = data.getString("message")
                instance.setCustomKey("Custom_Error_API_Service ", message)
                instance.log(message)
                instance.recordException(Exception(data.toString()))
                fileHelper.logException(Exception(data.toString()))
                return message
            } catch (e: Exception) {
                instance.recordException(e)
                fileHelper.logException(e)
                return e.localizedMessage.orEmpty()
            }
        }

        override suspend fun logout(userId: Int) {
            val response =
                try {
                    apiService.logout(userId)
                    true
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    fileHelper.logException(e)
                    throw e
                }

            if (!response) {
                error("Error logging out of the server")
            }
        }
    }
