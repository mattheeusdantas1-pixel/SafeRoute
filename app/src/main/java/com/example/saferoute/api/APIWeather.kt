package com.example.saferoute.api

import com.example.saferoute.model.Clima

// Os nomes dos atributos são iguais aos do JSON, por isso o underline
data class APICondition(
    var text: String? = null,
    var icon: String? = null
)

data class APIWeather(
    var last_updated: String? = null,
    var temp_c: Double? = 0.0,
    var maxtemp_c: Double? = 0.0,
    var mintemp_c: Double? = 0.0,
    var condition: APICondition? = null
)

data class APICurrentWeather(
    var location: APILocation? = null,
    var current: APIWeather? = null
)

fun APICurrentWeather.toClima(): Clima {
    return Clima(
        data = current?.last_updated ?: "...",
        desc = current?.condition?.text ?: "...",
        temp = current?.temp_c ?: -1.0,
        // A API manda o ícone sem o protocolo, então completamos
        imgUrl = "https:" + current?.condition?.icon
    )
}
