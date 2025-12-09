package com.ih.osm.domain.repository.network

import com.ih.osm.data.model.CiltEvidenceRequest
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.CreateCiltExecutionRequest
import com.ih.osm.data.model.CreateCiltExecutionResponse
import com.ih.osm.data.model.CreateDefinitiveSolutionRequest
import com.ih.osm.data.model.CreateProvisionalSolutionRequest
import com.ih.osm.data.model.FastLoginRequest
import com.ih.osm.data.model.GenerateCiltExecutionRequest
import com.ih.osm.data.model.GenerateCiltExecutionResponse
import com.ih.osm.data.model.GetPaginatedCardsResponse
import com.ih.osm.data.model.GetPaginatedLevelsResponse
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
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Catalogs
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.model.CiltProcedureData
import com.ih.osm.domain.model.CiltSequenceEvidence
import com.ih.osm.domain.model.Employee
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

    suspend fun getRemoteLevels(
        siteId: String,
        page: Int,
        limit: Int,
    ): GetPaginatedLevelsResponse

    suspend fun getRemotePreclassifiers(siteId: String): List<Preclassifier>

    suspend fun getRemotePriorities(siteId: String): List<Priority>

    // suspend fun getRemoteCardsByUser(siteId: String): List<Card>

    suspend fun getRemoteCardsByUser(
        siteId: String,
        page: Int? = null,
        limit: Int? = null,
    ): GetPaginatedCardsResponse

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

    suspend fun getRemoteCiltProcedureByLevel(levelId: String): CiltProcedureData

    suspend fun createCiltExecution(request: CreateCiltExecutionRequest): CreateCiltExecutionResponse

    suspend fun generateCiltExecution(request: GenerateCiltExecutionRequest): GenerateCiltExecutionResponse

    suspend fun refreshToken(body: RefreshTokenRequest): LoginResponse

//    /**
//     * Get cards by level ID with optional pagination
//     * @param levelId The level ID
//     * @param siteId The site ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of cards
//     */
//    suspend fun getRemoteCardsByLevel(
//        levelId: String,
//        siteId: String,
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Card>
//
//    /**
//     * Get levels with location data with optional pagination
//     * @param siteId The site ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of levels
//     */
//    suspend fun getRemoteLevelsWithLocation(
//        siteId: String,
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Level>
//
//    /**
//     * Get site levels with optional pagination
//     * @param siteId The site ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of levels
//     */
//    suspend fun getRemoteSiteLevels(
//        siteId: String,
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Level>
//
//    /**
//     * Get level tree with lazy loading support
//     * @param siteId The site ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @param depth Tree depth to fetch (optional)
//     * @return Level tree data with nested children
//     */
//    suspend fun getRemoteLevelTreeLazy(
//        siteId: String,
//        page: Int? = null,
//        limit: Int? = null,
//        depth: Int? = null,
//    ): LevelTreeData
//
//    /**
//     * Get children levels of a parent
//     * @param siteId The site ID
//     * @param parentId The parent level ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of child levels
//     */
//    suspend fun getRemoteChildrenLevels(
//        siteId: String,
//        parentId: String,
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Level>
//
//    /**
//     * Get level statistics for a site
//     * @param siteId The site ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return Level statistics
//     */
//    suspend fun getRemoteLevelStats(
//        siteId: String,
//        page: Int? = null,
//        limit: Int? = null,
//    ): LevelStats
//
//    /**
//     * Find a level by its machineId and get its full hierarchy path
//     * @param siteId The site ID
//     * @param machineId The levelMachineId to search for
//     * @return List of levels representing the hierarchy from root to found level
//     */
//    suspend fun findLevelByMachineId(
//        siteId: String,
//        machineId: String,
//    ): List<Level>

    suspend fun getCatalogsBySite(siteId: String): Catalogs
}
