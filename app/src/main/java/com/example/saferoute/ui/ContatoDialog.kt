package com.example.saferoute.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.saferoute.R

// Mesmo formato do CityDialog da Prática 03.
// O onConfirm devolve o erro (ou null): assim o diálogo continua aberto mostrando
// o problema, em vez de fechar e a pessoa perder o que digitou.
@Composable
fun ContatoDialog(
    onDismiss: () -> Unit,
    onConfirm: (nome: String, telefone: String, parentesco: String, email: String) -> Int?
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var telefone by rememberSaveable { mutableStateOf("") }
    var parentesco by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }
    // Só nome e telefone são obrigatórios
    val podeSalvar = nome.isNotBlank() && telefone.isNotEmpty()

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.large) {
            // Rolagem: com 4 campos, a mensagem de erro e o teclado, pode não caber tudo
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.contato_dialog_titulo),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { onDismiss() }) {
                        Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.fechar))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                DataField(
                    label = stringResource(R.string.campo_nome),
                    value = nome,
                    onValueChange = { nome = it; erro = null },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TelefoneField(
                    value = telefone,
                    onValueChange = { telefone = it; erro = null },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                DataField(
                    label = stringResource(R.string.campo_parentesco),
                    value = parentesco,
                    onValueChange = { parentesco = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                DataField(
                    label = stringResource(R.string.campo_email),
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Email
                )
                MensagemErro(erro, Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { erro = onConfirm(nome, telefone, parentesco, email) },
                    enabled = podeSalvar,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = coresBotaoPositivo()
                ) {
                    CadeadoDesabilitado(podeSalvar)
                    Text(stringResource(R.string.botao_salvar), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
