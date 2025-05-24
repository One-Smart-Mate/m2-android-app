package com.ih.osm.domain.repository.network

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.LogoutRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.SequenceExecutionRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.SequenceExecutionData

interface NetworkRepository {
    suspend fun login(data: LoginRequest): LoginResponse

    suspend fun sendRestorePasswordCode(data: RestorePasswordRequest)

    suspend fun verifyPasswordCode(data: RestorePasswordRequest)

    suspend fun resetPassword(data: RestorePasswordRequest)

    suspend fun updateToken(data: UpdateTokenRequest)

    suspend fun getRemoteCardTypes(siteId: String): List<CardType>

    suspend fun getRemoteEmployees(siteId: String): List<Employee>

    suspend fun getRemoteEmployeesByRole(
        siteId: String,
        roleName: String,
    ): List<Employee>

    suspend fun getRemoteLevels(siteId: String): List<Level>

    suspend fun getRemotePreclassifiers(siteId: String): List<Preclassifier>

    suspend fun getRemotePriorities(siteId: String): List<Priority>

    suspend fun getRemoteCardsByUser(siteId: String): List<Card>

    suspend fun getRemoteCardDetail(cardId: String): Card?

    suspend fun saveRemoteCard(card: CreateCardRequest): Card

    suspend fun getRemoteCardsZone(
        superiorId: String,
        siteId: String,
    ): List<Card>

    suspend fun saveRemoteDefinitiveSolution(createDefinitiveSolutionRequest: CreateDefinitiveSolutionRequest): Card

    suspend fun saveRemoteProvisionalSolution(createProvisionalSolutionRequest: CreateProvisionalSolutionRequest): Card

    suspend fun getRemoteCardsLevelMachine(
        levelMachine: String,
        siteId: String,
    ): List<Card>

    suspend fun updateRemoteMechanic(body: UpdateMechanicRequest)

    suspend fun logout(body: LogoutRequest)

    suspend fun getCilts(body: GetCiltsRequest): CiltData

    suspend fun updateSequenceExecution(body: SequenceExecutionRequest): SequenceExecutionData

    suspend fun createEvidence(body: CiltEvidenceRequest)
}
