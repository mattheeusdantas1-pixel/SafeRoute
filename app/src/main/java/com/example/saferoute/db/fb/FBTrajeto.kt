package com.example.saferoute.db.fb

import com.example.saferoute.model.StatusTrajeto
import com.example.saferoute.model.Trajeto

class FBTrajeto {
    var id: String? = null
    var inicio: Long? = null
    var previsaoChegada: Long? = null
    var adiamentos: Int? = null
    var fim: Long? = null
    var destino: String? = null
    var distanciaMetros: Double? = null
    var status: String? = null   // o enum vira texto: "SEGURO", "ALERTA"...

    fun toTrajeto(): Trajeto {
        return Trajeto(
            id = id!!,
            inicio = inicio!!,
            previsaoChegada = previsaoChegada!!,
            adiamentos = adiamentos ?: 0,
            fim = fim,
            destino = destino,
            distanciaMetros = distanciaMetros ?: 0.0,
            status = StatusTrajeto.valueOf(status ?: "SEGURO")
        )
    }
}

fun Trajeto.toFBTrajeto(): FBTrajeto {
    val fb = FBTrajeto()
    fb.id = this.id
    fb.inicio = this.inicio
    fb.previsaoChegada = this.previsaoChegada
    fb.adiamentos = this.adiamentos
    fb.fim = this.fim
    fb.destino = this.destino
    fb.distanciaMetros = this.distanciaMetros
    fb.status = this.status.name
    return fb
}
