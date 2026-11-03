package com.example.saferoute.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.saferoute.CONTAGEM_SOS_SEGUNDOS
import com.example.saferoute.MainViewModel
import com.example.saferoute.R
import com.example.saferoute.ui.theme.NumeroContagem
import kotlinx.coroutines.delay

// RF36: abre o discador já com o número. ACTION_DIAL só abre a tela de ligar,
// quem aperta "ligar" é a pessoa (RN17). Por isso não precisa de permissão.
private fun discar(context: Context, numero: String) {
    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))
}

@Composable
fun SosPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val sos = viewModel.sosAtual

    var segundos by rememberSaveable { mutableStateOf(CONTAGEM_SOS_SEGUNDOS) }
    var confirmando by rememberSaveable { mutableStateOf(false) }

    // Quando o SOS acaba (cancelou ou encerrou), sai da tela
    LaunchedEffect(sos == null) {
        if (sos == null) onVoltar()
    }

    // RN06: contagem regressiva. No zero, começa a "enviar"
    LaunchedEffect(Unit) {
        while (segundos > 0) {
            delay(1000)
            segundos--
        }
        viewModel.iniciarEnvios()
    }

    if (sos == null) return

    // RN08: pede confirmação antes de encerrar
    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            shape = MaterialTheme.shapes.large,
            title = { Text(stringResource(R.string.sos_encerrar_titulo)) },
            text = {
                Text(
                    text = stringResource(R.string.sos_encerrar_texto),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        confirmando = false
                        viewModel.encerrarSos()
                    },
                    colors = coresBotaoPositivo()
                ) {
                    Text(stringResource(R.string.sos_encerrar), style = MaterialTheme.typography.titleMedium)
                }
            },
            dismissButton = {
                BotaoContorno(onClick = { confirmando = false }) {
                    Text(stringResource(R.string.botao_cancelar), style = MaterialTheme.typography.titleMedium)
                }
            }
        )
    }

    // Em cada estado, o conteúdo fica numa área que rola e as ações ficam fixas embaixo:
    // numa emergência, ligar, cancelar e encerrar nunca podem ficar fora da tela
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (segundos > 0) {
            // Fase 1: contagem, dá tempo de cancelar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.sos_enviando_em),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.sos_segundos, segundos),
                    style = NumeroContagem,
                    color = MaterialTheme.colorScheme.error
                )
            }
            // Cancelar/Encerrar: sem vermelho, mas com borda forte para achar rápido, à noite e com pressa
            BotaoContorno(
                onClick = { viewModel.cancelarSos() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.sos_cancelar), style = MaterialTheme.typography.titleMedium)
            }
        } else {
            // Fase 2: SOS ativo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Triângulo = alerta (psicologia das formas). Enfeite: o texto logo abaixo já diz tudo
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = stringResource(R.string.sos_ativo),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Card(modifier = Modifier.fillMaxWidth(), colors = coresCard()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.sos_envios, sos.enviosRealizados),
                            style = MaterialTheme.typography.titleMedium
                        )
                        sos.ultimaLocalizacao?.let {
                            Text(
                                text = stringResource(R.string.sos_mensagem, it.linkMapa()),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Ações embaixo, na zona do polegar
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                BotaoEmergencia(
                    texto = stringResource(R.string.sos_ligar_190),
                    onClick = { discar(context, "190") }
                )
                BotaoEmergencia(
                    texto = stringResource(R.string.sos_ligar_192),
                    onClick = { discar(context, "192") }
                )
                BotaoContorno(
                    onClick = { confirmando = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(R.string.sos_encerrar), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

// Ligar 190/192: tela de emergência, por isso pode ser vermelho (botão comum nunca é)
@Composable
private fun BotaoEmergencia(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(72.dp),
        colors = coresBotaoSos()
    ) {
        Icon(imageVector = Icons.Filled.Phone, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text = texto, style = MaterialTheme.typography.titleLarge)
    }
}
