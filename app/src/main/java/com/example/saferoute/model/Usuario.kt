package com.example.saferoute.model

data class Usuario(
    val nome: String,
    val telefone: String,
    val gestoSacudirAtivo: Boolean = true
)
