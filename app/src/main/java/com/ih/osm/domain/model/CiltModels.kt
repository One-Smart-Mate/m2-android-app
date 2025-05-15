package com.ih.osm.domain.model

data class CiltData(
    val userInfo: UserInfo,
    val positions: List<Position>,
)

data class UserInfo(
    val id: String,
    val name: String,
    val email: String,
)

data class Position(
    val id: Int,
    val name: String,
    val siteName: String,
    val areaName: String,
    val ciltMasters: List<CiltMaster>,
)

data class CiltMaster(
    val id: Int,
    val siteId: Int,
    val positionId: Int,
    val ciltName: String,
    val ciltDescription: String,
    val creatorName: String,
    val reviewerName: String,
    val approvedByName: String,
    val urlImgLayout: String?,
    val updatedAt: String?,
    val status: String,
    val sequences: List<Sequence>,
)

data class Sequence(
    val id: Int,
    val levelName: String,
    val ciltTypeName: String,
    val secuenceList: String,
    val secuenceColor: String,
    val toolsRequired: String,
    val standardOk: String,
    val stoppageReason: Int,
    val machineStopped: Int,
    val referencePoint: String?,
    val status: String,
    val executions: List<Execution>,
)

data class Execution(
    val id: Int,
    val secuenceStart: String,
    val secuenceStop: String,
    val duration: Int,
    val initialParameter: String,
    val finalParameter: String,
)

// MOCK data
fun mockCiltData(): CiltData {
    return CiltData(
        userInfo =
            UserInfo(
                id = "1",
                name = "John Doe",
                email = "john.doe@example.com",
            ),
        positions =
            listOf(
                Position(
                    id = 101,
                    name = "Operator",
                    siteName = "Main Plant",
                    areaName = "Assembly",
                    ciltMasters =
                        listOf(
                            CiltMaster(
                                id = 1001,
                                siteId = 1,
                                positionId = 101,
                                ciltName = "CILT-001",
                                ciltDescription = "Main assembly CILT",
                                creatorName = "Alice",
                                reviewerName = "Bob",
                                approvedByName = "Carol",
                                urlImgLayout = "https://example.com/layout.png",
                                updatedAt = "2025-05-10T02:33:00.000Z",
                                status = "APPROVED",
                                sequences =
                                    listOf(
                                        Sequence(
                                            id = 2001,
                                            levelName = "Level 1",
                                            ciltTypeName = "Type A",
                                            secuenceList = "Step 1, Step 2, Step 3",
                                            secuenceColor = "#FF0000",
                                            toolsRequired = "Wrench, Screwdriver",
                                            standardOk = "Yes",
                                            stoppageReason = 0,
                                            machineStopped = 1,
                                            referencePoint = "Point A",
                                            status = "COMPLETED",
                                            executions =
                                                listOf(
                                                    Execution(
                                                        id = 3001,
                                                        secuenceStart = "2025-05-15T10:05:00Z",
                                                        secuenceStop = "2025-05-15T10:10:00Z",
                                                        duration = 5,
                                                        initialParameter = "Param A",
                                                        finalParameter = "Param B",
                                                    ),
                                                    Execution(
                                                        id = 3002,
                                                        secuenceStart = "2025-05-15T10:15:00Z",
                                                        secuenceStop = "2025-05-15T10:20:00Z",
                                                        duration = 5,
                                                        initialParameter = "Param C",
                                                        finalParameter = "Param D",
                                                    ),
                                                ),
                                        ),
                                    ),
                            ),
                        ),
                ),
            ),
    )
}
