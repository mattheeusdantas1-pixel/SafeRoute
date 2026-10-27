package com.example.saferoute.model

import java.util.UUID

data class AlertaSos(
    val id: String = UUID.randomUUID().toString(),
    val inicio: Long = System.currentTimeMillis(),
    val fim: Long? = null,
    val enviosRealizados: Int = 0,
    val ultimaLocalizacao: Localizacao? = null
)
