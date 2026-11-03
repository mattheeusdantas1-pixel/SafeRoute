package com.example.saferoute.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.saferoute.R
import com.example.saferoute.ui.theme.Nevoa
import com.example.saferoute.ui.theme.NoitePetroleo
import com.example.saferoute.ui.theme.Petroleo
import com.example.saferoute.ui.theme.SosVermelho
import com.example.saferoute.ui.theme.VerdeAgua

// Tela provisória: título + "em construção" + o que mais a tela quiser pôr embaixo
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

@Composable
fun DataField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
    )
}

// Campo de telefone: teclado numérico e máscara (81) 98888-7777.
// O valor guardado é só os números, no máximo 11.
@Composable
fun TelefoneField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { novo -> onValueChange(novo.filter { it.isDigit() }.take(11)) },
        label = { Text(stringResource(R.string.campo_telefone)) },
        placeholder = { Text(stringResource(R.string.exemplo_telefone)) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        visualTransformation = TelefoneVisualTransformation(),
        modifier = modifier
    )
}

// Campo de senha com o "olho" para mostrar/esconder
@Composable
fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var visivel by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visivel) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visivel = !visivel }) {
                Icon(
                    imageVector = if (visivel) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = stringResource(
                        if (visivel) R.string.ocultar_senha else R.string.mostrar_senha
                    )
                )
            }
        },
        modifier = modifier
    )
}

// Mostra a mensagem de erro (id do strings.xml) numa caixa de alerta; se for null não mostra nada.
// A caixa existe por causa do contraste: vermelho direto sobre o fundo Névoa fica difícil de ler.
@Composable
fun MensagemErro(erro: Int?, modifier: Modifier = Modifier) {
    if (erro != null) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = MaterialTheme.shapes.small,
            modifier = modifier.padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(erro),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

// Card Petróleo com texto Névoa nos dois modos, igual em todas as telas
@Composable
fun coresCard(): CardColors =
    CardDefaults.cardColors(containerColor = Petroleo, contentColor = Nevoa)

// Só para SOS e ligações de emergência. Usa SosVermelho direto porque o colorScheme.error
// do modo escuro é mais claro de propósito, e o texto branco dá contraste nos dois modos
@Composable
fun coresBotaoSos(): ButtonColors =
    ButtonDefaults.buttonColors(containerColor = SosVermelho, contentColor = Color.White)

// Ações positivas (iniciar, cheguei bem): Verde-água nos dois modos, com texto escuro para dar contraste.
// O desabilitado padrão do Material fica quase invisível, então usa as cores de superfície do tema
@Composable
fun coresBotaoPositivo(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = VerdeAgua,
        contentColor = NoitePetroleo,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

// Vai dentro do botão: o cadeado mostra que ele está desabilitado sem depender só da cor
@Composable
fun CadeadoDesabilitado(habilitado: Boolean) {
    if (!habilitado) {
        Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.size(8.dp))
    }
}

// Ação secundária (limpar, sair, remover): sem vermelho, com borda forte para aparecer nos dois modos
@Composable
fun BotaoContorno(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    conteudo: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        content = conteudo
    )
}
