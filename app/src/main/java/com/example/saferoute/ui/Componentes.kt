package com.example.saferoute.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.saferoute.R

// Tela provisória da fase 1: título + "em construção" + o que mais a tela quiser pôr embaixo
@Composable
fun PaginaProvisoria(
    titulo: String,
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, style = MaterialTheme.typography.headlineMedium)
        Text(text = stringResource(R.string.pagina_em_construcao))
        conteudo()
    }
}
