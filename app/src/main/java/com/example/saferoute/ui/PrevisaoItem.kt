package com.example.saferoute.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.saferoute.model.Previsao
import kotlin.math.roundToInt

// Um dia da previsão: data, descrição curta e mínima/máxima
@Composable
fun PrevisaoItem(previsao: Previsao, modifier: Modifier = Modifier) {
    // A API manda "2026-11-20". Cortando o texto vira "20/11", sem String.format
    val dia = if (previsao.data.length >= 10) {
        previsao.data.substring(8, 10) + "/" + previsao.data.substring(5, 7)
    } else {
        previsao.data
    }

    Card(modifier = modifier, colors = coresCard()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dia, style = MaterialTheme.typography.titleMedium)
            Text(
                text = previsao.desc,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${previsao.tempMin.roundToInt()}° / ${previsao.tempMax.roundToInt()}°",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
