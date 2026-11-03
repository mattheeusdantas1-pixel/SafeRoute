package com.example.saferoute.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.saferoute.R

// Mesma ideia do CityDialog da Prática 03: um diálogo que só recebe funções.
// Não fecha ao tocar fora: a pessoa precisa responder (ou o SOS dispara).
@Composable
fun CheckinDialog(
    podeAdiar: Boolean,
    onCheguei: () -> Unit,
    onAdiar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        shape = MaterialTheme.shapes.large,
        title = { Text(stringResource(R.string.trajeto_checkin_titulo)) },
        text = {
            Text(
                text = stringResource(R.string.trajeto_checkin_texto),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(onClick = onCheguei, colors = coresBotaoPositivo()) {
                Text(stringResource(R.string.trajeto_cheguei), style = MaterialTheme.typography.titleMedium)
            }
        },
        dismissButton = {
            // RN13: depois de 3 adiamentos o "Mais 15 min" some
            if (podeAdiar) {
                BotaoContorno(onClick = onAdiar) {
                    Text(stringResource(R.string.trajeto_mais_15), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    )
}
