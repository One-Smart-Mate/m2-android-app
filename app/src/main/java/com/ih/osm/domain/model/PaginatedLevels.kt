package com.ih.osm.domain.model

/**
 * Domain model for paginated level results
 *
 * This model wraps a page of levels with pagination metadata, enabling
 * infinite scroll and paged loading functionality.
 *
 * @property levels List of levels for the current page
 * @property currentPage Current page number (1-based)
 * @property pageSize Number of items per page
 * @property totalLevels Total number of levels across all pages
 * @property totalPages Total number of pages
 * @property hasNextPage Whether there are more pages to load
 */
data class PaginatedLevels(
    val levels: List<Level>,
    val currentPage: Int,
    val pageSize: Int,
    val totalLevels: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
) {
    /**
     * Checks if this is the first page
     */
    fun isFirstPage(): Boolean = currentPage == 1

    /**
     * Checks if this is the last page
     */
    fun isLastPage(): Boolean = currentPage >= totalPages || !hasNextPage

    /**
     * Gets the next page number if available
     */
    fun nextPageNumber(): Int? = if (hasNextPage) currentPage + 1 else null

    /**
     * Gets the previous page number if available
     */
    fun previousPageNumber(): Int? = if (currentPage > 1) currentPage - 1 else null

    companion object {
        /**
         * Creates an empty paginated result
         */
        fun empty(): PaginatedLevels =
            PaginatedLevels(
                levels = emptyList(),
                currentPage = 1,
                pageSize = 50,
                totalLevels = 0,
                totalPages = 0,
                hasNextPage = false,
            )
    }
}
