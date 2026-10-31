package com.example.saferoute.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferoute.CONTAGEM_SOS_SEGUNDOS
import com.example.saferoute.MainViewModel
import com.example.saferoute.R
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
            title = { Text(stringResource(R.string.sos_encerrar_titulo)) },
            text = { Text(stringResource(R.string.sos_encerrar_texto)) },
            confirmButton = {
                Button(onClick = {
                    confirmando = false
                    viewModel.encerrarSos()
                }) {
                    Text(stringResource(R.string.sos_encerrar))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmando = false }) {
                    Text(stringResource(R.string.botao_cancelar))
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        if (segundos > 0) {
            // Fase 1: contagem, dá tempo de cancelar
            Text(stringResource(R.string.sos_enviando_em), style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(R.string.sos_segundos, segundos),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            OutlinedButton(
                onClick = { viewModel.cancelarSos() },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.sos_cancelar))
            }
        } else {
            // Fase 2: SOS ativo
            Text(
                text = stringResource(R.string.sos_ativo),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(stringResource(R.string.sos_envios, sos.enviosRealizados))
            sos.ultimaLocalizacao?.let {
                Text(stringResource(R.string.sos_mensagem, it.linkMapa()))
            }
            Button(
                onClick = { discar(context, "190") },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.sos_ligar_190))
            }
            Button(
                onClick = { discar(context, "192") },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.sos_ligar_192))
            }
            OutlinedButton(
                onClick = { confirmando = true },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.sos_encerrar))
            }
        }
    }
}
