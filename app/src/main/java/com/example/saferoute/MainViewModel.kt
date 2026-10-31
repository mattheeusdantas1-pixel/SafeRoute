package com.example.saferoute

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.saferoute.model.AlertaSos
import com.example.saferoute.model.Contato
import com.example.saferoute.model.StatusTrajeto
import com.example.saferoute.model.Trajeto
import com.example.saferoute.model.Usuario

private const val MINUTO = 60_000L
private const val HORA = 60 * MINUTO

// RN01: no máximo 5 contatos de confiança
const val MAX_CONTATOS = 5

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
    // ===== CONTA E CONTATOS =====

    // "private set": a tela pode ler, mas só o ViewModel pode mudar.
    // Como é mutableStateOf, toda tela que lê o valor se redesenha quando ele muda.
    // Usuária de exemplo; o login troca pelos dados digitados (definirUsuario)
    var usuario by mutableStateOf(Usuario(nome = "Ana", telefone = "81999990000"))
        private set

    // Mesmo esquema das cidades da Prática 03: a lista de verdade é privada
    // e a tela só recebe uma cópia, então ninguém mexe nela por fora do ViewModel
    private val _contatos = contatosExemplo().toMutableStateList()
    val contatos
        get() = _contatos.toList()

    // Modo escuro escolhido no Perfil. Por enquanto só guarda a escolha;
    // o tema ainda não usa esse valor
    var modoEscuro by mutableStateOf(false)
        private set

    // Chamado pela MainActivity com os dados que vieram do login
    fun definirUsuario(nome: String, telefone: String) {
        usuario = usuario.copy(nome = nome, telefone = telefone)
    }

    // RF03: editar o nome no Perfil. Devolve null se salvou; se não, o id do erro
    fun alterarNome(nome: String): Int? {
        val erro = if (nome.isBlank()) R.string.erro_nome_vazio else null
        if (erro == null) {
            usuario = usuario.copy(nome = nome.trim())
        }
        return erro
    }

    // RF19: liga/desliga o gesto de sacudir o celular para pedir SOS
    fun alterarGestoSacudir(ativo: Boolean) {
        usuario = usuario.copy(gestoSacudirAtivo = ativo)
    }

    fun alterarModoEscuro(ativo: Boolean) {
        modoEscuro = ativo
    }

    // Devolve null se adicionou; se não, o id da mensagem de erro (R.string...)
    fun adicionarContato(nome: String, telefone: String, parentesco: String, email: String): Int? {
        val erro = when {
            _contatos.size >= MAX_CONTATOS -> R.string.erro_limite_contatos   // RN01
            nome.isBlank() -> R.string.erro_nome_contato_vazio
            validarTelefone(telefone) != null -> R.string.erro_telefone      // RN02
            // RN03: o mesmo telefone duas vezes mandaria o alerta repetido
            _contatos.any { it.telefone == telefone } -> R.string.erro_telefone_repetido
            else -> null
        }
        if (erro == null) {
            _contatos.add(
                Contato(
                    nome = nome.trim(),
                    telefone = telefone,
                    // Campos opcionais: em branco vira null, para a tela saber que não tem
                    parentesco = parentesco.trim().ifBlank { null },
                    email = email.trim().ifBlank { null }
                )
            )
        }
        return erro
    }

    fun removerContato(contato: Contato) {
        _contatos.remove(contato)
    }

    // ===== TRAJETO, SOS E HISTÓRICO =====

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
}
