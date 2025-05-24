package com.ih.osm.domain.model

data class Opl(
    val id: Int,
    val title: String,
    // Cambiado para coincidir con la API
    val objetive: String,
    val creatorId: Int,
    val creatorName: String,
    val siteId: String,
    val reviewerId: Int,
    val reviewerName: String,
    val oplType: String,
    // Cambiado a String para evitar problemas de parsing
    val createdAt: String,
    // Cambiado a String para evitar problemas de parsing
    val updatedAt: String,
    val deletedAt: String? = null,
    val details: List<OplDetail> = emptyList(),
) {
    companion object {
        fun mockOplList(): List<Opl> =
            listOf(
                Opl(
                    id = 1,
                    title = "OPL de Seguridad",
                    objetive = "Mejorar los procedimientos de seguridad en el área de producción",
                    creatorId = 92,
                    creatorName = "Erick",
                    siteId = "1",
                    reviewerId = 90,
                    reviewerName = "ImmaTest",
                    oplType = "opl",
                    createdAt = "2025-04-28T04:23:57.000Z",
                    updatedAt = "2025-05-16T21:39:48.000Z",
                    details =
                        listOf(
                            OplDetail(
                                id = 1,
                                oplId = 1,
                                order = 1,
                                type = "texto",
                                text = "Identificar los riesgos principales",
                                mediaUrl = "",
                            ),
                            OplDetail(
                                id = 2,
                                oplId = 1,
                                order = 2,
                                type = "texto",
                                text = "Implementar medidas preventivas",
                                mediaUrl = "",
                            ),
                        ),
                ),
                Opl(
                    id = 2,
                    title = "OPL de Mantenimiento",
                    objetive = "Establecer rutinas de mantenimiento preventivo",
                    creatorId = 93,
                    creatorName = "Carlos",
                    siteId = "1",
                    reviewerId = 90,
                    reviewerName = "ImmaTest",
                    oplType = "opl",
                    createdAt = "2025-04-28T04:23:57.000Z",
                    updatedAt = "2025-05-16T21:39:48.000Z",
                    details =
                        listOf(
                            OplDetail(
                                id = 3,
                                oplId = 2,
                                order = 1,
                                type = "texto",
                                text = "Revisar equipos diariamente",
                                mediaUrl = "",
                            ),
                        ),
                ),
                Opl(
                    id = 3,
                    title = "OPL de Calidad",
                    objetive = "Reducir defectos en la línea de producción",
                    creatorId = 94,
                    creatorName = "Ana",
                    siteId = "1",
                    reviewerId = 91,
                    reviewerName = "Pedro",
                    oplType = "opl",
                    createdAt = "2025-04-28T04:23:57.000Z",
                    updatedAt = "2025-05-16T21:39:48.000Z",
                    details = emptyList(),
                ),
            )
    }
}

data class OplDetail(
    val id: Int,
    val oplId: Int,
    val order: Int,
    val type: String,
    val text: String,
    val mediaUrl: String,
    val updatedAt: String = "2025-05-16T21:39:48.000Z",
)
