package com.example.saferoute.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    // Sugestões da busca enquanto a pessoa digita
    var sugestoes by mutableStateOf<List<Localizacao>>(emptyList())
        private set

    fun buscar(texto: String) {
        // Cancela a busca anterior para uma resposta velha não sobrescrever a nova
        service.cancelAll()
        if (texto.length < 3) {
            sugestoes = emptyList()
            return
        }
        service.search(texto) { res ->
            // null é busca cancelada ou falha: mantém as sugestões como estavam
            res?.let { lista ->
                sugestoes = lista.mapNotNull { item ->
                    val lat = item.lat
                    val lon = item.lon
                    val nome = item.name
                    if (lat != null && lon != null && nome != null) {
                        // Nome e região, para diferenciar cidades com o mesmo nome
                        Localizacao(lat, lon, listOfNotNull(nome, item.region).joinToString(", "))
                    } else null
                }
            }
        }
    }

    fun limparSugestoes() {
        service.cancelAll()
        sugestoes = emptyList()
    }

    override fun onCleared() {
        service.cancelAll()
    }
}
