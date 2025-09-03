package com.ih.osm.domain.repository.network

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.FastLoginRequest
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.LogoutRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.SendFastPasswordRequest
import com.ih.osm.data.model.SendFastPasswordResponse
import com.ih.osm.data.model.StartSequenceExecutionRequest
import com.ih.osm.data.model.StopSequenceExecutionRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.Sequence
import com.ih.osm.domain.model.SequenceExecution

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

    suspend fun getRemoteOplsByLevel(levelId: String): List<Opl>

    suspend fun logout(body: LogoutRequest)

    suspend fun getCilts(
        userId: String,
        date: String,
    ): CiltData

    suspend fun getOplById(id: String): Opl

    suspend fun startSequenceExecution(body: StartSequenceExecutionRequest): SequenceExecution

    suspend fun stopSequenceExecution(body: StopSequenceExecutionRequest): SequenceExecution

    suspend fun createEvidence(body: CiltEvidenceRequest): CiltSequenceEvidence

    suspend fun fastLogin(body: FastLoginRequest): LoginResponse

    suspend fun getSequence(id: Int): Sequence

    suspend fun sendFastPassword(body: SendFastPasswordRequest): SendFastPasswordResponse
}
