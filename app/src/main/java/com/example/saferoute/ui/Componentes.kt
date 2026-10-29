package com.example.saferoute.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.saferoute.R

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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visivel) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visivel = !visivel }) {
                Icon(
                    imageVector = if (visivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = stringResource(
                        if (visivel) R.string.ocultar_senha else R.string.mostrar_senha
                    )
                )
            }
        },
        modifier = modifier
    )
}

// Mostra a mensagem de erro (id do strings.xml) em vermelho; se for null não mostra nada
@Composable
fun MensagemErro(erro: Int?, modifier: Modifier = Modifier) {
    if (erro != null) {
        Text(
            text = stringResource(erro),
            color = MaterialTheme.colorScheme.error,
            modifier = modifier.padding(top = 8.dp)
        )
    }
}
