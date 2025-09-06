package com.ih.osm.domain.model

data class Procedure(
    val id: Int,
    val title: String,
    val objective: String,
    val creatorId: Int,
    val creatorName: String,
    val siteId: String,
    val reviewerId: Int,
    val reviewerName: String,
    val procedureType: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null,
    val details: List<ProcedureDetail> = emptyList(),
) {
    companion object {
        fun mockProcedureList(): List<Procedure> =
            listOf(
                Procedure(
                    id = 1,
                    title = "Procedimiento de Limpieza",
                    objective = "Establecer métodos de limpieza efectivos",
                    creatorId = 92,
                    creatorName = "Erick",
                    siteId = "1",
                    reviewerId = 90,
                    reviewerName = "ImmaTest",
                    procedureType = "procedure",
                    createdAt = "2025-04-28T04:23:57.000Z",
                    updatedAt = "2025-05-16T21:39:48.000Z",
                    details =
                        listOf(
                            ProcedureDetail(
                                id = 1,
                                procedureId = 1,
                                order = 1,
                                type = "texto",
                                text = "Preparar materiales de limpieza",
                                mediaUrl = "",
                            ),
                            ProcedureDetail(
                                id = 2,
                                procedureId = 1,
                                order = 2,
                                type = "texto",
                                text = "Aplicar técnicas de limpieza",
                                mediaUrl = "",
                            ),
                        ),
                ),
                Procedure(
                    id = 2,
                    title = "Procedimiento de Arranque",
                    objective = "Pasos para arranque seguro de equipos",
                    creatorId = 93,
                    creatorName = "Carlos",
                    siteId = "1",
                    reviewerId = 90,
                    reviewerName = "ImmaTest",
                    procedureType = "procedure",
                    createdAt = "2025-04-28T04:23:57.000Z",
                    updatedAt = "2025-05-16T21:39:48.000Z",
                    details =
                        listOf(
                            ProcedureDetail(
                                id = 3,
                                procedureId = 2,
                                order = 1,
                                type = "texto",
                                text = "Verificar condiciones iniciales",
                                mediaUrl = "",
                            ),
                        ),
                ),
                Procedure(
                    id = 3,
                    title = "Procedimiento de Paro",
                    objective = "Método seguro para parar equipos",
                    creatorId = 94,
                    creatorName = "Ana",
                    siteId = "1",
                    reviewerId = 91,
                    reviewerName = "Pedro",
                    procedureType = "procedure",
                    createdAt = "2025-04-28T04:23:57.000Z",
                    updatedAt = "2025-05-16T21:39:48.000Z",
                    details = emptyList(),
                ),
            )
    }
}

data class ProcedureDetail(
    val id: Int,
    val procedureId: Int,
    val order: Int,
    val type: String,
    val text: String,
    val mediaUrl: String,
    val updatedAt: String = "2025-05-16T21:39:48.000Z",
)
