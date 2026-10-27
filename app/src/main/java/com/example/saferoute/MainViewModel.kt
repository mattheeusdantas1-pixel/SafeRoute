package com.example.saferoute

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.saferoute.model.AlertaSos
import com.example.saferoute.model.Contato
import com.example.saferoute.model.StatusTrajeto
import com.example.saferoute.model.Trajeto
import com.example.saferoute.model.Usuario

private const val MINUTO = 60_000L
private const val HORA = 60 * MINUTO

// Dados de exemplo para as telas não abrirem vazias na demo
private fun contatosExemplo() = listOf(
    Contato(nome = "Maria Silva", telefone = "81988887777", parentesco = "Mãe"),
    Contato(nome = "João Santos", telefone = "81977776666", parentesco = "Irmão")
)

private fun historicoExemplo(): List<Trajeto> {
    val agora = System.currentTimeMillis()
    val ontem = agora - 24 * HORA
    val anteontem = agora - 48 * HORA
    return listOf(
        Trajeto(
            inicio = ontem, previsaoChegada = ontem + 30 * MINUTO, fim = ontem + 25 * MINUTO,
            destino = "Casa", distanciaMetros = 2400.0, status = StatusTrajeto.SEGURO
        ),
        Trajeto(
            inicio = anteontem, previsaoChegada = anteontem + 20 * MINUTO, fim = anteontem + 40 * MINUTO,
            destino = "Faculdade", distanciaMetros = 1800.0, status = StatusTrajeto.ALERTA
        )
    )
}

class MainViewModel : ViewModel() {
    // "private set": a tela pode ler, mas só o ViewModel pode mudar.
    // Como é mutableStateOf, toda tela que lê o valor se redesenha quando ele muda.
    // Usuária de exemplo; o login troca pelos dados digitados (definirUsuario)
    var usuario by mutableStateOf(Usuario(nome = "Ana", telefone = "81999990000"))
        private set

    var contatos by mutableStateOf(contatosExemplo())
        private set

    // null = nenhum trajeto ativo agora
    var trajetoAtual by mutableStateOf<Trajeto?>(null)
        private set

    // true = o horário previsto venceu e a tela tem que perguntar "Chegou bem?"
    var checkinPendente by mutableStateOf(false)
        private set

    // null = nenhum SOS ativo agora
    var sosAtual by mutableStateOf<AlertaSos?>(null)
        private set

    var historico by mutableStateOf(historicoExemplo())
        private set

    // Chamado pela MainActivity com os dados que vieram do login
    fun definirUsuario(nome: String, telefone: String) {
        usuario = usuario.copy(nome = nome, telefone = telefone)
    }
}
