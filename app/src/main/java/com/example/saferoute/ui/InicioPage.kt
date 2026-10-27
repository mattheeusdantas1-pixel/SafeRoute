package com.example.saferoute.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.saferoute.MainViewModel
import com.example.saferoute.R

@Composable
fun InicioPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onSosClick: () -> Unit
) {
    PaginaProvisoria(
        titulo = stringResource(R.string.ola_usuario, viewModel.usuario.nome),
        modifier = modifier
    ) {
        Button(onClick = onSosClick) {
            Text(stringResource(R.string.botao_sos))
        }
    }
}
