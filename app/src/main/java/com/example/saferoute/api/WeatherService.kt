package com.example.saferoute.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {
    private var weatherAPI: WeatherServiceAPI

    // Chamadas em andamento, para poder cancelar quando a pessoa digita rápido
    private val searches = mutableListOf<Call<List<APILocation>?>>()

    init {
        val retrofitAPI = Retrofit.Builder()
            .baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }

    // Coordenadas para nome do lugar. Entrega null se não achar ou se falhar
    fun getName(lat: Double, lng: Double, onResponse: (String?) -> Unit) {
        search("$lat,$lng") { list ->
            onResponse(list?.firstOrNull()?.name)
        }
    }

    // Nome para coordenadas. Pega só o primeiro resultado, porque buscar por
    // nome costuma devolver várias cidades parecidas
    fun getLocation(name: String, onResponse: (lat: Double?, long: Double?) -> Unit) {
        search(name) { list ->
            val primeiro = list?.firstOrNull()
            onResponse(primeiro?.lat, primeiro?.lon)
        }
    }

    // Busca genérica. enqueue roda em segundo plano, porque rede não pode
    // travar a thread que desenha a tela
    fun search(query: String, onResponse: (List<APILocation>?) -> Unit) {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        searches.add(call)
        call.enqueue(object : Callback<List<APILocation>?> {
            override fun onResponse(
                call: Call<List<APILocation>?>,
                response: Response<List<APILocation>?>
            ) {
                onResponse(response.body())
                searches.remove(call)
            }

            override fun onFailure(call: Call<List<APILocation>?>, t: Throwable) {
                Log.w("SafeRoute WARNING", "" + t.message)
                onResponse(null)
                searches.remove(call)
            }
        })
    }

    // Função genérica: serve para qualquer tipo de resposta
    private fun <T> enqueue(call: Call<T?>, onResponse: ((T?) -> Unit)? = null) {
        call.enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                onResponse?.invoke(response.body())
            }

            override fun onFailure(call: Call<T?>, t: Throwable) {
                Log.w("SafeRoute WARNING", "" + t.message)
            }
        })
    }

    // Clima atual de um ponto. O query pode ser "lat,lng"
    fun getWeather(query: String, onResponse: (APICurrentWeather?) -> Unit) {
        val call: Call<APICurrentWeather?> = weatherAPI.weather(query)
        enqueue(call) { onResponse.invoke(it) }
    }

    // Previsão dos próximos dias de um ponto. O query pode ser "lat,lng"
    fun getForecast(query: String, onResponse: (APIWeatherForecast?) -> Unit) {
        val call: Call<APIWeatherForecast?> = weatherAPI.forecast(query)
        enqueue(call) { onResponse.invoke(it) }
    }

    // Cancelar dispara onFailure, que entrega null. Quem recebe null ignora
    fun cancelAll() {
        searches.toList().forEach { it.cancel() }
    }
}
