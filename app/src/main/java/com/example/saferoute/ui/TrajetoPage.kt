package com.example.saferoute.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.saferoute.MainViewModel
import com.example.saferoute.R

@Composable
fun TrajetoPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    PaginaProvisoria(titulo = stringResource(R.string.aba_trajeto), modifier = modifier)
}
