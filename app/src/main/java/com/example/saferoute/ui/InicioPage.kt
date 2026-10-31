package com.example.saferoute.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferoute.MAX_CONTATOS
import com.example.saferoute.MainViewModel
import com.example.saferoute.R

@Composable
fun InicioPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onSosClick: () -> Unit
) {
    val activity = LocalActivity.current as Activity

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.ola_usuario, viewModel.usuario.nome),
            style = MaterialTheme.typography.headlineMedium
        )

        // Botão redondo, grande e vermelho: fácil de achar numa emergência
        Button(
            onClick = {
                // RN05: sem contatos o ViewModel devolve o erro e a tela de SOS não abre
                val erro = viewModel.acionarSos()
                if (erro != null) {
                    Toast.makeText(activity, erro, Toast.LENGTH_LONG).show()
                } else {
                    onSosClick()
                }
            },
            modifier = Modifier.size(180.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(
                text = stringResource(R.string.botao_sos),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(stringResource(R.string.inicio_dica_sos))

        // Resumo: quantos contatos tem e se há trajeto em andamento
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.inicio_resumo_contatos, viewModel.contatos.size, MAX_CONTATOS))
                Text(
                    stringResource(
                        if (viewModel.trajetoAtual != null) R.string.inicio_trajeto_ativo
                        else R.string.inicio_trajeto_nenhum
                    )
                )
            }
        }
    }
}
