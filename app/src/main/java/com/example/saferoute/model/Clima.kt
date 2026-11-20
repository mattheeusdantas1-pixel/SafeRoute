package com.example.saferoute.model

import android.graphics.Bitmap

// Clima atual na forma que a tela precisa
data class Clima(
    val data: String,
    val desc: String,
    val temp: Double,
    val imgUrl: String,
    var bitmap: Bitmap? = null   // usado na Prática 9, no marcador do mapa
) {
    companion object {
        // Marcador de "ainda carregando"
        val CARREGANDO = Clima(
            data = "CARREGANDO", desc = "CARREGANDO",
            temp = -1.0, imgUrl = "CARREGANDO", bitmap = null
        )
    }
}
