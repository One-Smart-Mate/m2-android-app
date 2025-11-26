package com.ih.osm.domain.usecase.level

import android.util.Log
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

/**
 * UseCase for finding a level by its machineId
 *
 * This UseCase searches for a level using its levelMachineId and returns
 * the full hierarchy path from root to the found level.
 *
 * Business Logic:
 * - Validates machineId is not blank
 * - Searches for level in remote API
 * - Returns hierarchy array from root to found level
 * - Returns error if not found or if search fails
 *
 * Used for quick level selection by scanning or entering machine ID
 */
interface FindLevelByMachineIdUseCase {
    /**
     * Find a level by its machineId
     *
     * @param machineId The levelMachineId to search for (must not be blank)
     * @return Result wrapping list of levels (hierarchy from root to found level)
     */
    suspend operator fun invoke(machineId: String): Result<List<Level>>
}

class FindLevelByMachineIdUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
    ) : FindLevelByMachineIdUseCase {
        companion object {
            private const val TAG = "FindLevelByMachineIdUseCase"
        }

        override suspend fun invoke(machineId: String): Result<List<Level>> {
            Log.d(TAG, "===== EXECUTE: machineId=$machineId =====")

            // Validate input
            if (machineId.isBlank()) {
                Log.e(TAG, "ERROR: Machine ID cannot be blank")
                return Result.Error("REQUIRED")
            }

            return try {
                Log.d(TAG, "Searching for machineId: $machineId")

                // Search in repository
                val hierarchy = levelRepository.findByMachineId(machineId.trim())

                // Validate response
                when {
                    hierarchy.isEmpty() -> {
                        Log.w(TAG, "WARN: Empty hierarchy returned for machineId: $machineId")
                        Result.Error("NOT_FOUND")
                    }
                    else -> {
                        Log.d(TAG, "SUCCESS: Found hierarchy with ${hierarchy.size} levels")
                        hierarchy.forEachIndexed { index, level ->
                            Log.d(TAG, "  Level $index: id=${level.id}, name=${level.name}")
                        }
                        Result.Success(hierarchy)
                    }
                }
            } catch (e: NullPointerException) {
                Log.e(TAG, "ERROR: Null response data", e)
                Result.Error("NOT_FOUND")
            } catch (e: Exception) {
                Log.e(TAG, "ERROR: ${e.message}", e)
                Result.Error("SEARCH_ERROR")
            }
        }
    }
