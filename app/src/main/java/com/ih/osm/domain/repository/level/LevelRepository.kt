package com.ih.osm.domain.repository.level

import com.ih.osm.data.model.GetPaginatedLevelsResponse
import com.ih.osm.domain.model.Level

interface LevelRepository {
    suspend fun getAllRemote(
        page: Int,
        limit: Int,
    ): GetPaginatedLevelsResponse

    suspend fun saveAll(list: List<Level>)

    suspend fun getAll(): List<Level>

    suspend fun get(id: String): Level?

    suspend fun deleteAll()

//    /**
//     * Get levels with location data from remote with optional pagination
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of levels with location data
//     */
//    suspend fun getRemoteLevelsWithLocation(
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Level>
//
//    /**
//     * Get site levels from remote with optional pagination
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of site levels
//     */
//    suspend fun getRemoteSiteLevels(
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Level>
//
//    /**
//     * Get level tree with lazy loading support from remote
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @param depth Tree depth to fetch (optional)
//     * @return Level tree data with nested children
//     */
//    suspend fun getRemoteLevelTreeLazy(
//        page: Int? = null,
//        limit: Int? = null,
//        depth: Int? = null,
//    ): LevelTreeData
//
//    /**
//     * Get children levels of a parent from remote
//     * @param parentId The parent level ID
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return List of child levels
//     */
//    suspend fun getRemoteChildrenLevels(
//        parentId: String,
//        page: Int? = null,
//        limit: Int? = null,
//    ): List<Level>
//
//    /**
//     * Get level statistics from remote
//     * @param page Page number (optional)
//     * @param limit Items per page (optional)
//     * @return Level statistics
//     */
//    suspend fun getRemoteLevelStats(
//        page: Int? = null,
//        limit: Int? = null,
//    ): LevelStats
//
//    /**
//     * Find a level by its machineId and get its full hierarchy path
//     * @param machineId The levelMachineId to search for
//     * @return List of levels representing the hierarchy from root to found level
//     */
//    suspend fun findByMachineId(machineId: String): List<Level>
}
