package com.example.saferoute.model

// Previsão de um dia na forma que a tela precisa
data class Previsao(
    val data: String,
    val desc: String,
    val tempMin: Double,
    val tempMax: Double,
    val imgUrl: String
)
