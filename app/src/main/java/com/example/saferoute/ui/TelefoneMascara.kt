package com.example.saferoute.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// "81988887777" -> "(81) 98888-7777"   |   "8133334444" -> "(81) 3333-4444"
fun formatarTelefone(digitos: String): String {
    if (digitos.isEmpty()) return ""
    val ddd = digitos.take(2)
    val resto = digitos.drop(2)
    // Celular (11 dígitos) tem 5 antes do traço; fixo (10) tem 4
    val antesDoTraco = if (digitos.length == 11) 5 else 4

    var formatado = "($ddd"
    if (resto.isNotEmpty()) {
        formatado += ") " + resto.take(antesDoTraco)
        if (resto.length > antesDoTraco) formatado += "-" + resto.drop(antesDoTraco)
    }
    return formatado
}

// O campo guarda só os números; esta classe só muda o que aparece na tela.
// O OffsetMapping diz onde o cursor fica, já que o texto mostrado tem ( ) e - a mais.
class TelefoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formatado = formatarTelefone(text.text)

        val mapa = object : OffsetMapping {
            // Posição nos números digitados -> posição no texto com máscara
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                var digitosVistos = 0
                formatado.forEachIndexed { i, c ->
                    if (c.isDigit()) {
                        digitosVistos++
                        if (digitosVistos == offset) return i + 1
                    }
                }
                return formatado.length
            }

            // Posição no texto com máscara -> quantos números vêm antes dela
            override fun transformedToOriginal(offset: Int): Int =
                formatado.take(offset).count { it.isDigit() }
        }
        return TransformedText(AnnotatedString(formatado), mapa)
    }
}
