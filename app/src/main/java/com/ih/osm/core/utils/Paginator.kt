package com.ih.osm.core.utils

// Generic interface for paginated response
interface PaginatedResponse<T> {
    val data: List<T>
    val totalPages: Int?
    val total: Int?
    val hasMore: Boolean?
    val page: Int?
    val limit: Int?
}

// Generic Paginator helper
object Paginator {
    /**
     * Fetch all pages from the API and save in batches.
     * @param pageLimit Number of items per API call
     * @param batchSize Number of items to save per batch
     * @param fetchPage Lambda to fetch a single page: suspend (page, limit) -> PaginatedResponse<T>
     * @param saveBatch Lambda to save a batch of items: suspend (List<T>) -> Unit
     */
    suspend fun <T> fetchAll(
        pageLimit: Int,
        batchSize: Int,
        fetchPage: suspend (page: Int, limit: Int) -> PaginatedResponse<T>,
        saveBatch: suspend (List<T>) -> Unit,
    ): List<T> {
        val allItems = mutableListOf<T>()
        var page = 1
        var totalPages = 1

        do {
            val response = fetchPage(page, pageLimit)
            val items = response.data
            allItems.addAll(items)

            // Save in batches
            var batchStart = 0
            while (batchStart < items.size) {
                val batchEnd = minOf(batchStart + batchSize, items.size)
                val batch = items.subList(batchStart, batchEnd)
                saveBatch(batch)
                batchStart = batchEnd
            }

            totalPages = response.totalPages ?: totalPages
            page++
        } while (page <= totalPages)

        return allItems
    }
}
