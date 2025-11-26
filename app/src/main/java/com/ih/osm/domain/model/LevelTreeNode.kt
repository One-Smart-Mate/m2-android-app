package com.ih.osm.domain.model

/**
 * Domain model representing a node in the lazy-loaded level tree
 *
 * This model wraps a Level with additional metadata for tree hierarchy
 * and lazy loading support. It tracks whether children have been loaded
 * and provides placeholders for unloaded children.
 *
 * @property level The level data for this node
 * @property hasChildren Whether this node has child levels
 * @property childrenCount Number of children this node has
 * @property isLoaded Whether children have been loaded for this node
 * @property children List of child nodes (empty if not loaded, contains placeholder if hasChildren but not loaded)
 */
data class LevelTreeNode(
    val level: Level,
    val hasChildren: Boolean,
    val childrenCount: Int,
    val isLoaded: Boolean,
    val children: List<LevelTreeNode> = emptyList(),
) {
    /**
     * Returns the level ID
     */
    val id: String get() = level.id

    /**
     * Returns the level name
     */
    val name: String get() = level.name

    /**
     * Checks if this node needs to load children
     * (has children but they haven't been loaded yet)
     */
    fun needsChildrenLoading(): Boolean = hasChildren && !isLoaded

    /**
     * Checks if this is a placeholder node
     */
    fun isPlaceholder(): Boolean = level.id.endsWith("_placeholder")

    /**
     * Creates a copy of this node with loaded children
     */
    fun withChildren(loadedChildren: List<LevelTreeNode>): LevelTreeNode =
        copy(
            children = loadedChildren,
            isLoaded = true,
        )

    /**
     * Creates a copy of this node marking it as expanded/collapsed
     */
    fun toggleExpanded(): LevelTreeNode =
        if (isLoaded) {
            copy(children = if (children.isEmpty()) emptyList() else children)
        } else {
            this
        }

    companion object {
        /**
         * Creates a tree node from a Level with unknown children status
         *
         * @param level The level to wrap
         * @param hasChildren Whether the level has children (from backend or inferred)
         * @param childrenCount Number of children (default 0)
         * @return LevelTreeNode with unloaded children
         */
        fun from(
            level: Level,
            hasChildren: Boolean = false,
            childrenCount: Int = 0,
        ): LevelTreeNode =
            LevelTreeNode(
                level = level,
                hasChildren = hasChildren,
                childrenCount = childrenCount,
                isLoaded = false,
                children = emptyList(),
            )

        /**
         * Creates a tree node from a Level with pre-loaded children
         *
         * @param level The level to wrap
         * @param children Pre-loaded child nodes
         * @return LevelTreeNode with loaded children
         */
        fun fromWithChildren(
            level: Level,
            children: List<LevelTreeNode>,
        ): LevelTreeNode =
            LevelTreeNode(
                level = level,
                hasChildren = children.isNotEmpty(),
                childrenCount = children.size,
                isLoaded = true,
                children = children,
            )

        /**
         * Creates a placeholder node for lazy loading
         *
         * @param parentId ID of the parent node
         * @return Placeholder LevelTreeNode
         */
        fun createPlaceholder(parentId: String): LevelTreeNode =
            LevelTreeNode(
                level =
                    Level(
                        id = "${parentId}_placeholder",
                        ownerId = null,
                        ownerName = null,
                        superiorId = parentId,
                        name = "...",
                        description = "Loading...",
                        status = "",
                    ),
                hasChildren = false,
                childrenCount = 0,
                isLoaded = false,
                children = emptyList(),
            )
    }
}
