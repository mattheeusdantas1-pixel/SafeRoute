package com.example.saferoute.model

import java.util.UUID

// O id é gerado sozinho e serve de key na LazyColumn (dois contatos podem ter o mesmo nome)
data class Contato(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val telefone: String,
    val parentesco: String? = null,
    val email: String? = null
)
