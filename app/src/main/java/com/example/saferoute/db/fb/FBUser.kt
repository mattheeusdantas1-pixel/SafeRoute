package com.example.saferoute.db.fb

import com.example.saferoute.model.Usuario

class FBUser {
    var nome: String? = null
    var email: String? = null
    var telefone: String? = null
    var gestoSacudirAtivo: Boolean? = null

    fun toUsuario(): Usuario {
        return Usuario(
            nome = nome ?: "",
            telefone = telefone ?: "",
            gestoSacudirAtivo = gestoSacudirAtivo ?: true,
            email = email ?: ""
        )
    }
}

fun Usuario.toFBUser(): FBUser {
    val fb = FBUser()
    fb.nome = this.nome
    fb.email = this.email
    fb.telefone = this.telefone
    fb.gestoSacudirAtivo = this.gestoSacudirAtivo
    return fb
}
