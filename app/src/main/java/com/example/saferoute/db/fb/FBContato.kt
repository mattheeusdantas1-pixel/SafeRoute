package com.example.saferoute.db.fb

import com.example.saferoute.model.Contato

class FBContato {
    var id: String? = null
    var nome: String? = null
    var telefone: String? = null
    var parentesco: String? = null
    var email: String? = null

    fun toContato(): Contato {
        return Contato(
            id = id!!,
            nome = nome ?: "",
            telefone = telefone ?: "",
            parentesco = parentesco,
            email = email
        )
    }
}

fun Contato.toFBContato(): FBContato {
    val fb = FBContato()
    fb.id = this.id
    fb.nome = this.nome
    fb.telefone = this.telefone
    fb.parentesco = this.parentesco
    fb.email = this.email
    return fb
}
