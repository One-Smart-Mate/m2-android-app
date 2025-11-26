package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Result
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

interface FindLevelByMachineIdUseCase {
    suspend operator fun invoke(machineId: String): Result<List<Level>>
}

class FindLevelByMachineIdUseCaseImpl
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
    ) : FindLevelByMachineIdUseCase {
        override suspend fun invoke(machineId: String): Result<List<Level>> {
            if (machineId.isBlank()) {
                return Result.Error("REQUIRED")
            }

            return try {
                val hierarchy = levelRepository.findByMachineId(machineId.trim())

                when {
                    hierarchy.isEmpty() -> Result.Error("NOT_FOUND")
                    else -> Result.Success(hierarchy)
                }
            } catch (e: NullPointerException) {
                Result.Error("NOT_FOUND")
            } catch (e: Exception) {
                Result.Error("SEARCH_ERROR")
            }
        }
    }
