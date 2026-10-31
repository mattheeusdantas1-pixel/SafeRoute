package com.example.saferoute.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.saferoute.MainViewModel
import com.example.saferoute.R
import com.example.saferoute.model.StatusTrajeto
import com.example.saferoute.model.Trajeto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatarData(ms: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date(ms))

private fun formatarDuracao(inicio: Long, fim: Long?): String {
    if (fim == null) return "em andamento"

    val minutos = (fim - inicio) / 60000
    return "$minutos min"
}

@Composable
private fun ItemHistorico(trajeto: Trajeto) {
    val seguro = trajeto.status == StatusTrajeto.SEGURO
    val emAndamento = trajeto.status == StatusTrajeto.EM_ANDAMENTO

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    seguro -> Icons.Filled.CheckCircle
                    emAndamento -> Icons.Filled.Schedule
                    else -> Icons.Filled.Warning
                },
                contentDescription = when {
                    seguro -> "Seguro"
                    emAndamento -> "Em andamento"
                    else -> "Alerta"
                }
            )

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    trajeto.destino ?: "Sem destino",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(formatarData(trajeto.inicio))

                Text("Duração: ${formatarDuracao(trajeto.inicio, trajeto.fim)}")

                Text(
                    "Distância: ${
                        "%.1f".format(
                            Locale("pt", "BR"),
                            trajeto.distanciaMetros / 1000
                        )
                    } km"
                )

                Text(
                    when {
                        seguro -> "Seguro"
                        emAndamento -> "Em andamento"
                        else -> "Alerta"
                    }
                )
            }
        }
    }
}

@Composable
fun HistoricoPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val itens = viewModel.historico

    if (itens.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.historico_vazio))
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(itens, key = { it.id }) { trajeto ->
            ItemHistorico(trajeto)
        }
    }
}