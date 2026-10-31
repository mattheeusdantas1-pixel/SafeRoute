package com.example.saferoute.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
        title = { Text(stringResource(R.string.trajeto_checkin_titulo)) },
        text = { Text(stringResource(R.string.trajeto_checkin_texto)) },
        confirmButton = {
            Button(onClick = onCheguei) {
                Text(stringResource(R.string.trajeto_cheguei))
            }
        },
        dismissButton = {
            // RN13: depois de 3 adiamentos o "Mais 15 min" some
            if (podeAdiar) {
                OutlinedButton(onClick = onAdiar) {
                    Text(stringResource(R.string.trajeto_mais_15))
                }
            }
        }
    )
}
