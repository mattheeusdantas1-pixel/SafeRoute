package com.example.saferoute.ui

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.saferoute.MainViewModel
import com.example.saferoute.R

@Composable
fun SosPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onVoltar: () -> Unit
) {
    PaginaProvisoria(titulo = stringResource(R.string.titulo_sos), modifier = modifier) {
        OutlinedButton(onClick = onVoltar) {
            Text(stringResource(R.string.voltar))
        }
    }
}
