package com.example.saferoute.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferoute.MAX_CONTATOS
import com.example.saferoute.MainViewModel
import com.example.saferoute.R
import com.example.saferoute.model.Contato

// Card em retângulo arredondado (forma "estável", da aula de IHC)
@Composable
fun ContatoCard(
    contato: Contato,
    onRemover: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // O nome já é lido logo ao lado, então o ícone é só decoração
            Icon(Icons.Filled.Person, contentDescription = null)
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = contato.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = formatarTelefone(contato.telefone), fontSize = 16.sp)
                // Opcionais: só aparecem se foram preenchidos
                contato.parentesco?.let { Text(text = it, fontSize = 16.sp) }
                contato.email?.let { Text(text = it, fontSize = 16.sp) }
            }
            IconButton(onClick = onRemover) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.remover_contato_desc, contato.nome)
                )
            }
        }
    }
}

@Composable
fun ContatosPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val contatos = viewModel.contatos
    val activity = LocalActivity.current as Activity
    val cheio = contatos.size >= MAX_CONTATOS

    var showDialog by rememberSaveable { mutableStateOf(false) }
    // Guarda só o id de quem vai ser removido (null = nenhuma confirmação aberta)
    var idParaRemover by rememberSaveable { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.contatos_titulo),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.contatos_contador, contatos.size, MAX_CONTATOS),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (contatos.isEmpty()) {
            Text(
                text = stringResource(R.string.contatos_vazio),
                fontSize = 16.sp,
                modifier = Modifier.weight(1f).padding(top = 16.dp)
            )
        } else {
            // weight(1f): a lista ocupa o meio e o botão fica sempre embaixo
            LazyColumn(modifier = Modifier.weight(1f)) {
                // key pelo id, nunca pelo nome: nome repetido fecharia o app
                items(contatos, key = { it.id }) { contato ->
                    ContatoCard(
                        contato = contato,
                        onRemover = { idParaRemover = contato.id }
                    )
                }
            }
        }

        if (cheio) {
            Text(
                text = stringResource(R.string.contatos_limite),
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        // Botão embaixo, na zona do polegar. Com 5 contatos fica desabilitado (RN01)
        Button(
            onClick = { showDialog = true },
            enabled = !cheio,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.botao_adicionar_contato), fontSize = 16.sp)
        }
    }

    if (showDialog) {
        ContatoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { nome, telefone, parentesco, email ->
                val erro = viewModel.adicionarContato(nome, telefone, parentesco, email)
                if (erro == null) {
                    showDialog = false
                    Toast.makeText(activity, R.string.contato_adicionado, Toast.LENGTH_SHORT).show()
                }
                erro
            }
        )
    }

    // Procura pelo id na lista atual; se não achar, não mostra nada
    val contatoParaRemover = contatos.find { it.id == idParaRemover }
    if (contatoParaRemover != null) {
        AlertDialog(
            onDismissRequest = { idParaRemover = null },
            title = { Text(stringResource(R.string.remover_contato_titulo)) },
            text = { Text(stringResource(R.string.remover_contato_texto, contatoParaRemover.nome)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removerContato(contatoParaRemover)
                        idParaRemover = null
                        Toast.makeText(activity, R.string.contato_removido, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    // Vermelho só aqui: é uma ação que apaga
                    Text(stringResource(R.string.botao_remover), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { idParaRemover = null }) {
                    Text(stringResource(R.string.botao_cancelar))
                }
            }
        )
    }
}
