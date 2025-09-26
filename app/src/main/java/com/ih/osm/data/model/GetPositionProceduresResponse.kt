package com.ih.osm.data.model

// Reutilizando la estructura exacta del modelo existente para mantener compatibilidad
data class GetPositionProceduresResponse(
    val data: List<CiltProcedurePosition>,
)
