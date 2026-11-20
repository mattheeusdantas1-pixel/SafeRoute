package com.example.saferoute.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.saferoute.api.WeatherService
import com.example.saferoute.api.toClima
import com.example.saferoute.api.toPrevisao
import com.example.saferoute.model.Clima
import com.example.saferoute.model.Localizacao
import com.example.saferoute.model.Previsao
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

    // Clima atual guardado por ponto ("lat,lng"). Mapa de estado: quando muda, a tela recompõe
    private val _clima = mutableStateMapOf<String, Clima>()

    // Devolve o clima do ponto. Na primeira vez dispara a busca e devolve CARREGANDO
    fun clima(local: Localizacao): Clima {
        val chave = "${local.latitude},${local.longitude}"
        return _clima.getOrPut(chave) {
            carregarClima(chave)
            Clima.CARREGANDO
        }
    }

    private fun carregarClima(chave: String) {
        service.getWeather(chave) { api ->
            api?.let {
                _clima[chave] = it.toClima()
                carregarBitmap(chave)
            }
        }
    }

    // Baixa o ícone como Bitmap para o marcador do mapa
    private fun carregarBitmap(chave: String) {
        _clima[chave]?.let { clima ->
            service.getBitmap(clima.imgUrl) { bitmap ->
                // copy cria uma cópia trocando só o bitmap; a mudança no mapa recompõe a tela
                if (bitmap != null) _clima[chave] = clima.copy(bitmap = bitmap)
            }
        }
    }

    private val _previsao = mutableStateMapOf<String, List<Previsao>?>()

    // Lista vazia enquanto carrega. Quando chega, o mapa de estado muda e a tela recompõe
    fun previsao(local: Localizacao): List<Previsao>? {
        val chave = "${local.latitude},${local.longitude}"
        return _previsao.getOrPut(chave) {
            carregarPrevisao(chave)
            emptyList()
        }
    }

    private fun carregarPrevisao(chave: String) {
        service.getForecast(chave) { api ->
            // Se a API não trouxe dias, guarda lista vazia: com null o getOrPut buscaria de novo
            api?.let { _previsao[chave] = it.toPrevisao() ?: emptyList() }
        }
    }

    override fun onCleared() {
        service.cancelAll()
    }
}
