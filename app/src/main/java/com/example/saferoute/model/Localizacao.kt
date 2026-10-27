package com.example.saferoute.model

data class Localizacao(
    val latitude: Double,
    val longitude: Double,
    val endereco: String? = null,
    val horario: Long = System.currentTimeMillis()
) {
    // Link que vai na mensagem do SOS, abre o ponto no Google Maps
    fun linkMapa() = "https://maps.google.com/?q=$latitude,$longitude"
}
