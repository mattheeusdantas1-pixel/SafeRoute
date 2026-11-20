package com.example.saferoute.ui

import androidx.lifecycle.ViewModel
import com.example.saferoute.api.WeatherService
import com.example.saferoute.model.Localizacao
import com.google.android.gms.maps.model.LatLng

// Guarda a conversa com a API de lugares fora da tela
class LugarViewModel(private val service: WeatherService) : ViewModel() {

    // Nome digitado para coordenadas. Entrega null se não achou
    fun resolverDestino(nome: String, onResponse: (Localizacao?) -> Unit) {
        service.getLocation(nome) { lat, lng ->
            if (lat != null && lng != null) {
                onResponse(Localizacao(lat, lng, nome))
            } else {
                onResponse(null)
            }
        }
    }

    // Ponto do mapa para nome do lugar. Entrega null se não achou
    fun nomeDoLugar(ponto: LatLng, onResponse: (Localizacao?) -> Unit) {
        service.getName(ponto.latitude, ponto.longitude) { nome ->
            if (nome != null) {
                onResponse(Localizacao(ponto.latitude, ponto.longitude, nome))
            } else {
                onResponse(null)
            }
        }
    }

    override fun onCleared() {
        service.cancelAll()
    }
}
