package com.example.saferoute.api

import com.example.saferoute.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Só a descrição do endpoint. Quem executa é o Retrofit
interface WeatherServiceAPI {
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }

    // O q aceita nome ("Recife") ou coordenadas ("-8.05,-34.9")
    @GET("search.json?key=$API_KEY&lang=pt_br")
    fun search(@Query("q") query: String): Call<List<APILocation>?>
}
