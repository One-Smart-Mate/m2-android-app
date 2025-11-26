package com.ih.osm.domain.cache

import android.content.Context
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.LevelStats
import com.ih.osm.domain.repository.level.LevelRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache manager for level data with TTL support
 *
 * This manager handles caching of level data, children, and statistics
 * with time-to-live (TTL) validation. It uses Room database for data
 * and SharedPreferences for cache metadata.
 *
 * Cache TTL: 5 minutes for level data
 */
@Singleton
class LevelCacheManager
    @Inject
    constructor(
        private val levelRepository: LevelRepository,
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            private const val CACHE_TTL_MS = 5 * 60 * 1000L // 5 minutes
            private const val PREFIX_CHILDREN = "level_children_timestamp_"
            private const val PREFIX_TREE = "level_tree_timestamp_"
            private const val PREFIX_STATS = "level_stats_timestamp"
            private const val PREFS_NAME = "level_cache_prefs"
        }

        private val prefs by lazy {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        /**
         * Gets cached children for a parent level
         *
         * Returns null if cache is invalid or expired
         *
         * @param parentId The parent level ID
         * @return List of cached child levels or null if cache miss
         */
        suspend fun getCachedChildren(parentId: String): List<Level>? {
            val cacheKey = "$PREFIX_CHILDREN$parentId"
            if (!isCacheValid(cacheKey)) {
                return null
            }

            val allLevels = levelRepository.getAll()
            val children = allLevels.filter { it.superiorId == parentId }

            return if (children.isNotEmpty()) children else null
        }

        /**
         * Caches a single level
         *
         * @param level The level to cache
         */
        suspend fun cacheLevel(level: Level) {
            // Save to database via repository
            levelRepository.saveAll(listOf(level))
        }

        /**
         * Caches tree node data with children
         *
         * @param parentId The parent level ID (null for root)
         * @param depth The depth fetched
         * @param levels The levels to cache
         */
        suspend fun cacheTreeNode(
            parentId: String?,
            depth: Int,
            levels: List<Level>,
        ) {
            // Save all levels to database
            levelRepository.saveAll(levels)

            // Update cache timestamp
            val cacheKey = "$PREFIX_TREE${parentId ?: "root"}_$depth"
            setCacheTimestamp(cacheKey, System.currentTimeMillis())
        }

        /**
         * Caches children levels for a parent
         *
         * @param parentId The parent level ID
         * @param children The child levels to cache
         */
        suspend fun cacheChildren(
            parentId: String,
            children: List<Level>,
        ) {
            levelRepository.saveAll(children)

            val cacheKey = "$PREFIX_CHILDREN$parentId"
            setCacheTimestamp(cacheKey, System.currentTimeMillis())
        }

        /**
         * Caches level statistics
         *
         * Note: Statistics are stored in SharedPreferences as they're lightweight
         *
         * @param stats The statistics to cache
         */
        fun cacheStats(stats: LevelStats) {
            val cacheKey = PREFIX_STATS

            // Store stats as JSON in SharedPreferences
            prefs.edit().apply {
                putString("${cacheKey}_data", serializeStats(stats))
                putLong(cacheKey, System.currentTimeMillis())
                apply()
            }
        }

        /**
         * Gets cached statistics
         *
         * @return Cached statistics or null if cache miss/expired
         */
        fun getCachedStats(): LevelStats? {
            val cacheKey = PREFIX_STATS

            if (!isCacheValid(cacheKey)) {
                return null
            }

            val statsJson = prefs.getString("${cacheKey}_data", null)
            return statsJson?.let { deserializeStats(it) }
        }

        /**
         * Clears all cached level data
         */
        suspend fun clearCache() {
            // Clear database
            levelRepository.deleteAll()

            // Clear SharedPreferences cache timestamps
            prefs.edit().apply {
                // Remove all level-related cache timestamps
                val allKeys = prefs.all.keys
                allKeys.forEach { key ->
                    if (key.startsWith(PREFIX_CHILDREN) ||
                        key.startsWith(PREFIX_TREE) ||
                        key.startsWith(PREFIX_STATS)
                    ) {
                        remove(key)
                    }
                }
                apply()
            }
        }

        /**
         * Gets cache timestamp for a key
         *
         * @param key Cache key
         * @return Timestamp in milliseconds or null if not found
         */
        fun getCacheTimestamp(key: String): Long? = prefs.getLong(key, -1L).takeIf { it != -1L }

        /**
         * Sets cache timestamp for a key
         *
         * @param key Cache key
         * @param timestamp Timestamp in milliseconds
         */
        fun setCacheTimestamp(
            key: String,
            timestamp: Long,
        ) {
            prefs.edit().apply {
                putLong(key, timestamp)
                apply()
            }
        }

        /**
         * Checks if cache is still valid based on TTL
         *
         * @param key Cache key
         * @return true if cache is valid, false otherwise
         */
        private fun isCacheValid(key: String): Boolean {
            val timestamp = getCacheTimestamp(key) ?: return false
            val age = System.currentTimeMillis() - timestamp
            return age < CACHE_TTL_MS
        }

        /**
         * Serializes LevelStats to JSON string
         */
        private fun serializeStats(stats: LevelStats): String =
            buildString {
                append("{")
                append("\"totalLevels\":${stats.totalLevels},")
                append("\"activeLevels\":${stats.activeLevels},")
                append("\"inactiveLevels\":${stats.inactiveLevels},")
                append("\"rootLevels\":${stats.rootLevels},")
                append("\"maxDepth\":${stats.maxDepth},")
                append("\"performanceWarning\":${stats.performanceWarning}")
                append("}")
            }

        /**
         * Deserializes LevelStats from JSON string
         */
        private fun deserializeStats(json: String): LevelStats? =
            try {
                // Simple JSON parsing (could use Gson if needed)
                val totalLevels = json.substringAfter("\"totalLevels\":").substringBefore(",").toInt()
                val activeLevels = json.substringAfter("\"activeLevels\":").substringBefore(",").toInt()
                val inactiveLevels = json.substringAfter("\"inactiveLevels\":").substringBefore(",").toInt()
                val rootLevels = json.substringAfter("\"rootLevels\":").substringBefore(",").toInt()
                val maxDepth = json.substringAfter("\"maxDepth\":").substringBefore(",").toInt()
                val performanceWarning =
                    json.substringAfter("\"performanceWarning\":").substringBefore("}").toBoolean()

                LevelStats(
                    totalLevels = totalLevels,
                    activeLevels = activeLevels,
                    inactiveLevels = inactiveLevels,
                    rootLevels = rootLevels,
                    maxDepth = maxDepth,
                    performanceWarning = performanceWarning,
                )
            } catch (e: Exception) {
                null
            }
    }
