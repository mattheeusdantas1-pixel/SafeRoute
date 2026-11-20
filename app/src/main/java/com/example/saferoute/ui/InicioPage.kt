package com.example.saferoute.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.saferoute.MAX_CONTATOS
import com.example.saferoute.MainViewModel
import com.example.saferoute.R
import com.example.saferoute.model.Clima
import com.example.saferoute.model.Localizacao
import com.example.saferoute.ui.theme.NoitePetroleo
import com.example.saferoute.ui.theme.SosVermelho
import com.example.saferoute.ui.theme.VerdeAgua

@Composable
fun InicioPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    lugarViewModel: LugarViewModel,
    onSosClick: () -> Unit
) {
    val activity = LocalActivity.current as Activity

    // BoxWithConstraints informa a altura disponível: o SOS encolhe em tela baixa,
    // porque ele nunca pode ficar cortado ou escondido atrás de uma rolagem
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val tamanhoSos = (maxHeight * 0.4f).coerceIn(120.dp, 200.dp)
        // Em tela muito baixa a dica sai para sobrar lugar para a saudação e o resumo
        val telaBaixa = maxHeight < 360.dp

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // weight: esta parte fica com o que sobrar e, se não couber, ela é que rola
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.ola_usuario, viewModel.usuario.nome),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                CardResumo(
                    totalContatos = viewModel.contatos.size,
                    trajetoAtivo = viewModel.trajetoAtual != null
                )

                CardClima(lugarViewModel)
            }

            // Fora da rolagem: o SOS fica sempre embaixo, na zona do polegar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                BotaoSos(
                    tamanho = tamanhoSos,
                    onClick = {
                        // RN05: sem contatos o ViewModel devolve o erro e a tela de SOS não abre
                        val erro = viewModel.acionarSos()
                        if (erro != null) {
                            Toast.makeText(activity, erro, Toast.LENGTH_LONG).show()
                        } else {
                            onSosClick()
                        }
                    }
                )

                if (!telaBaixa) {
                    Text(
                        text = stringResource(R.string.inicio_dica_sos),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Círculo grande com dois anéis em volta: é a forma mais fácil de achar numa emergência.
// Os anéis são proporcionais ao botão, para acompanhar quando ele encolhe.
@Composable
private fun BotaoSos(tamanho: Dp, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(tamanho * 1.32f)
                .border(1.dp, SosVermelho.copy(alpha = 0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(tamanho * 1.16f)
                .background(SosVermelho.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, SosVermelho.copy(alpha = 0.45f), CircleShape)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(tamanho)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = SosVermelho,
                    spotColor = SosVermelho
                ),
            shape = CircleShape,
            // Sem o recuo padrão do botão: no círculo menor ele quebrava o texto em "SO" e "S"
            contentPadding = PaddingValues(0.dp),
            colors = coresBotaoSos()
        ) {
            Text(
                text = stringResource(R.string.botao_sos),
                style = MaterialTheme.typography.displayLarge,
                maxLines = 1
            )
        }
    }
}

// A localização do app é simulada em Recife, então o clima do Início é o de Recife
private val RECIFE = Localizacao(-8.05, -34.9, "Recife")

// Clima atual de Recife. Fica na parte que rola, para o SOS não sair do lugar
@Composable
private fun CardClima(lugarViewModel: LugarViewModel) {
    val clima = lugarViewModel.clima(RECIFE)
    Card(modifier = Modifier.fillMaxWidth(), colors = coresCard()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.clima_agora),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (clima == Clima.CARREGANDO) {
                    stringResource(R.string.clima_carregando)
                } else {
                    stringResource(R.string.clima_agora_linha, clima.desc, clima.temp)
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// Resumo: quantos contatos tem e se há trajeto em andamento
@Composable
private fun CardResumo(totalContatos: Int, trajetoAtivo: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), colors = coresCard()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LinhaResumo(
                icone = Icons.Outlined.Group,
                texto = stringResource(R.string.inicio_resumo_contatos, totalContatos, MAX_CONTATOS)
            )

            if (trajetoAtivo) {
                // Pílula verde-água: o estado positivo salta aos olhos sem usar vermelho
                Surface(
                    color = VerdeAgua,
                    contentColor = NoitePetroleo,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    LinhaResumo(
                        icone = Icons.Outlined.Route,
                        texto = stringResource(R.string.inicio_trajeto_ativo),
                        corIcone = NoitePetroleo,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            } else {
                LinhaResumo(
                    icone = Icons.Outlined.Route,
                    texto = stringResource(R.string.inicio_trajeto_nenhum)
                )
            }
        }
    }
}

// contentDescription null: o ícone só enfeita, o leitor de tela já lê o texto ao lado
@Composable
private fun LinhaResumo(
    icone: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    corIcone: Color = VerdeAgua
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = corIcone,
            modifier = Modifier.size(20.dp)
        )
        Text(text = texto, style = MaterialTheme.typography.bodyLarge)
    }
}
