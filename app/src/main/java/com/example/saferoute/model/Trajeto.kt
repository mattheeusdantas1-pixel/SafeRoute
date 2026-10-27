package com.example.saferoute.model

import java.util.UUID

enum class StatusTrajeto { EM_ANDAMENTO, SEGURO, ALERTA }

// Datas e horas em milissegundos (Long), igual a System.currentTimeMillis()
data class Trajeto(
    val id: String = UUID.randomUUID().toString(),
    val inicio: Long = System.currentTimeMillis(),
    val previsaoChegada: Long,
    val adiamentos: Int = 0,
    val fim: Long? = null,
    val destino: String? = null,
    val distanciaMetros: Double = 0.0,
    val status: StatusTrajeto = StatusTrajeto.EM_ANDAMENTO
)
