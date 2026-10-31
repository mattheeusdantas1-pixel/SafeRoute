package com.example.saferoute.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.saferoute.MAX_ADIAMENTOS
import com.example.saferoute.METROS_POR_MINUTO
import com.example.saferoute.MainViewModel
import com.example.saferoute.R
import kotlinx.coroutines.delay
import java.util.Locale

// Transforma milissegundos em "mm:ss". Se for negativo (já passou), mostra 00:00
private fun formatarTempo(ms: Long): String {
    val totalSegundos = maxOf(ms, 0L) / 1000
    return "%02d:%02d".format(totalSegundos / 60, totalSegundos % 60)
}

@Composable
fun TrajetoPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onSosClick: () -> Unit
) {
    val trajeto = viewModel.trajetoAtual

    var destino by rememberSaveable { mutableStateOf("") }
    var minutos by rememberSaveable { mutableStateOf("30") }   // RN12: sugestão de 30 min
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }

    // "Relógio" da tela: atualiza a cada segundo para o tempo decorrido/restante andar
    var agora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            agora = System.currentTimeMillis()
        }
    }

    // Se o SOS foi acionado (por exemplo, o check-in ficou sem resposta), abre a tela de SOS
    LaunchedEffect(viewModel.sosAtual) {
        if (viewModel.sosAtual != null) onSosClick()
    }

    // RF30: o horário previsto passou, pergunta "Chegou bem?"
    if (viewModel.checkinPendente && trajeto != null) {
        CheckinDialog(
            podeAdiar = trajeto.adiamentos < MAX_ADIAMENTOS,
            onCheguei = { viewModel.encerrarTrajeto() },
            onAdiar = { erro = viewModel.adiarChegada() }
        )
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.trajeto_titulo),
            style = MaterialTheme.typography.headlineMedium
        )

        if (trajeto == null) {
            // Ainda não tem trajeto: mostra o formulário
            DataField(
                label = stringResource(R.string.trajeto_campo_destino),
                value = destino,
                onValueChange = { destino = it; erro = null },
                modifier = Modifier.fillMaxWidth()
            )
            DataField(
                label = stringResource(R.string.trajeto_campo_minutos),
                value = minutos,
                onValueChange = { minutos = it.filter { c -> c.isDigit() }.take(3); erro = null },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number
            )
            MensagemErro(erro)
            Button(
                onClick = {
                    val previsao = System.currentTimeMillis() + (minutos.toLongOrNull() ?: 0L) * 60_000L
                    erro = viewModel.iniciarTrajeto(destino, previsao)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.trajeto_iniciar))
            }
        } else {
            // Tem trajeto: mostra os tempos
            val decorrido = agora - trajeto.inicio
            val minutosAndados = decorrido / 60_000
            val km = minutosAndados * METROS_POR_MINUTO / 1000

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = trajeto.destino ?: stringResource(R.string.trajeto_sem_destino),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(stringResource(R.string.trajeto_decorrido, formatarTempo(decorrido)))
                    Text(stringResource(R.string.trajeto_restante, formatarTempo(trajeto.previsaoChegada - agora)))
                    Text(stringResource(R.string.trajeto_distancia, "%.1f".format(Locale.forLanguageTag("pt-BR"), km)))
                }
            }
            MensagemErro(erro)
            Button(
                onClick = { viewModel.encerrarTrajeto() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.trajeto_cheguei))
            }
        }
    }
}
