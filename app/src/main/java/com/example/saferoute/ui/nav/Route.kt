package com.example.saferoute.ui.nav

import kotlinx.serialization.Serializable

// Cada tela é uma rota. O @Serializable deixa o Navigation usar o próprio objeto como endereço.
sealed interface Route {
    @Serializable
    data object Inicio : Route
    @Serializable
    data object Trajeto : Route
    @Serializable
    data object Contatos : Route
    @Serializable
    data object Historico : Route
    @Serializable
    data object Perfil : Route
    // Fora da barra inferior: só abre pelo botão SOS
    @Serializable
    data object Sos : Route
}
