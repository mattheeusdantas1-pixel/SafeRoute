package com.example.saferoute

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saferoute.model.AlertaSos
import com.example.saferoute.model.Contato
import com.example.saferoute.model.Localizacao
import com.example.saferoute.model.StatusTrajeto
import com.example.saferoute.model.Trajeto
import com.example.saferoute.model.Usuario
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MINUTO = 60_000L
private const val HORA = 60 * MINUTO

// RN01: no máximo 5 contatos de confiança
const val MAX_CONTATOS = 5

// ===== Constantes do trajeto e do SOS =====
const val CONTAGEM_SOS_SEGUNDOS = 5          // RN06: tempo para cancelar antes de enviar
const val INTERVALO_SOS_MS = 60_000L         // RN07: reenvio a cada 1 minuto
const val MAX_ADIAMENTOS = 3                 // RN13: no máximo 3 vezes "Mais 15 min"
const val METROS_POR_MINUTO = 80.0           // distância simulada (GPS de verdade fica para a 3ª etapa)
private const val ADIAMENTO_MS = 15 * MINUTO
const val TEMPO_RESPOSTA_CHECKIN_MS = 5 * MINUTO

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

    // Guardam as "tarefas que esperam" para poder cancelar quando precisar
    private var jobCheckin: Job? = null
    private var jobSos: Job? = null

    // RF09, RF29, RN09: começa um trajeto. Devolve null se deu certo, ou o id do erro
    fun iniciarTrajeto(destino: String, previsaoChegada: Long): Int? {
        val agora = System.currentTimeMillis()
        val erro = when {
            trajetoAtual != null -> R.string.erro_trajeto_ja_ativo
            previsaoChegada <= agora -> R.string.erro_horario_passado
            else -> null
        }
        if (erro == null) {
            trajetoAtual = Trajeto(
                inicio = agora,
                previsaoChegada = previsaoChegada,
                destino = destino.trim().ifBlank { null }
            )
            agendarCheckin()
        }
        return erro
    }

    // RF30: espera o horário previsto, pergunta "Chegou bem?", e se ninguém responder aciona o SOS
    private fun agendarCheckin() {
        jobCheckin?.cancel()
        val previsao = trajetoAtual?.previsaoChegada ?: return
        jobCheckin = viewModelScope.launch {
            delay(previsao - System.currentTimeMillis())
            checkinPendente = true
            delay(TEMPO_RESPOSTA_CHECKIN_MS)
            if (checkinPendente) {
                checkinPendente = false
                acionarSos()
            }
        }
    }

    // RF30, RN13: "Mais 15 min", no máximo 3 vezes
    fun adiarChegada(): Int? {
        val trajeto = trajetoAtual ?: return null
        if (trajeto.adiamentos >= MAX_ADIAMENTOS) return R.string.erro_limite_adiamentos
        trajetoAtual = trajeto.copy(
            previsaoChegada = System.currentTimeMillis() + ADIAMENTO_MS,
            adiamentos = trajeto.adiamentos + 1
        )
        checkinPendente = false
        agendarCheckin()
        return null
    }

    // RF11: "Cheguei bem". O trajeto vai para o histórico
    fun encerrarTrajeto() {
        val trajeto = trajetoAtual ?: return
        jobCheckin?.cancel()
        checkinPendente = false
        val agora = System.currentTimeMillis()
        val minutos = (agora - trajeto.inicio) / MINUTO
        val terminado = trajeto.copy(
            fim = agora,
            distanciaMetros = minutos * METROS_POR_MINUTO,
            // se um SOS tinha marcado como ALERTA, continua ALERTA
            status = if (trajeto.status == StatusTrajeto.ALERTA) StatusTrajeto.ALERTA else StatusTrajeto.SEGURO
        )
        historico = listOf(terminado) + historico
        trajetoAtual = null
    }

    // RF14, RN05, RN09: aciona o SOS. A contagem de 5 segundos fica na SosPage.
    fun acionarSos(): Int? {
        if (_contatos.isEmpty()) return R.string.erro_sos_sem_contatos   // RN05
        if (sosAtual != null) return null                                 // já está ativo
        jobCheckin?.cancel()
        checkinPendente = false
        // RN09: se tinha trajeto, ele vira ALERTA
        trajetoAtual = trajetoAtual?.copy(status = StatusTrajeto.ALERTA)
        sosAtual = AlertaSos()
        return null
    }

    // RN07: depois da contagem, começa a "enviar" e repete a cada 1 minuto
    fun iniciarEnvios() {
        if (sosAtual == null || jobSos != null) return
        jobSos = viewModelScope.launch {
            while (sosAtual != null) {
                // Na 2ª etapa não manda SMS de verdade: só conta e guarda a localização (fixa em Recife)
                val local = Localizacao(latitude = -8.05, longitude = -34.9, endereco = "Recife")
                sosAtual = sosAtual?.copy(
                    enviosRealizados = (sosAtual?.enviosRealizados ?: 0) + 1,
                    ultimaLocalizacao = local
                )
                delay(INTERVALO_SOS_MS)
            }
        }
    }

    // RN06: cancelou durante a contagem, nada foi enviado
    fun cancelarSos() {
        jobSos?.cancel()
        jobSos = null
        sosAtual = null
        trajetoAtual = trajetoAtual?.copy(status = StatusTrajeto.EM_ANDAMENTO)
        agendarCheckin()
    }

    // RF17, RN08: encerrou o SOS. Vai para o histórico como ALERTA
    fun encerrarSos() {
        val sos = sosAtual ?: return
        jobSos?.cancel()
        jobSos = null
        val agora = System.currentTimeMillis()
        val trajeto = trajetoAtual
        val registro = if (trajeto != null) {
            trajeto.copy(fim = agora, status = StatusTrajeto.ALERTA)
        } else {
            // SOS sem trajeto: cria um registro só para aparecer no histórico
            Trajeto(
                inicio = sos.inicio,
                previsaoChegada = agora,
                fim = agora,
                destino = "SOS",
                status = StatusTrajeto.ALERTA
            )
        }
        historico = listOf(registro) + historico
        trajetoAtual = null
        sosAtual = null
        checkinPendente = false
    }
}
