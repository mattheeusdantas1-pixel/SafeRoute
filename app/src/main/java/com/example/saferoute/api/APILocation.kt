package com.example.saferoute.api

// Um item da resposta de search.json. Os nomes dos atributos têm que ser
// iguais aos nomes dos campos do JSON, senão o GSON não preenche
data class APILocation(
    var id: String? = null,
    var name: String? = null,
    var region: String? = null,
    var country: String? = null,
    var lat: Double? = null,
    var lon: Double? = null,
    var url: String? = null
)
