package com.example.saferoute.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.example.saferoute.LoginActivity
import com.example.saferoute.MainViewModel
import com.example.saferoute.R

// Uma linha com o texto à esquerda e o switch à direita.
// A linha tem 56dp de altura (mais que os 48dp mínimos) para ficar fácil de acertar com o dedo.
@Composable
fun LinhaSwitch(
    texto: String,
    marcado: Boolean,
    onMudar: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Switch(checked = marcado, onCheckedChange = onMudar)
    }
}

@Composable
fun PerfilPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val usuario = viewModel.usuario
    val activity = LocalActivity.current as Activity

    // Cópia local do nome: a pessoa digita à vontade e só vai para o ViewModel no Salvar
    var nome by rememberSaveable { mutableStateOf(usuario.nome) }
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.conta_titulo),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        DataField(
            label = stringResource(R.string.campo_nome),
            value = nome,
            onValueChange = { nome = it; erro = null },
            modifier = Modifier.fillMaxWidth()
        )
        MensagemErro(erro)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                erro = viewModel.alterarNome(nome)
                if (erro == null) {
                    Toast.makeText(activity, R.string.conta_nome_salvo, Toast.LENGTH_SHORT).show()
                }
            },
            // Só faz sentido salvar se o nome mudou
            enabled = nome != usuario.nome,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(stringResource(R.string.botao_salvar), fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // readOnly: aparece igual aos outros campos, mas não deixa editar.
        // O telefone é a "identidade" da conta, por isso não muda (RF04)
        OutlinedTextField(
            value = formatarTelefone(usuario.telefone),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.campo_telefone)) },
            supportingText = { Text(stringResource(R.string.conta_telefone_info)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Os switches leem o valor do ViewModel e avisam o ViewModel quando mudam
        LinhaSwitch(
            texto = stringResource(R.string.conta_gesto_sacudir),
            marcado = usuario.gestoSacudirAtivo,
            onMudar = { viewModel.alterarGestoSacudir(it) }
        )
        LinhaSwitch(
            texto = stringResource(R.string.conta_modo_escuro),
            marcado = viewModel.modoEscuro,
            onMudar = { viewModel.alterarModoEscuro(it) }
        )

        // weight(1f) empurra o Sair para baixo, na zona do polegar
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = {
                Toast.makeText(activity, R.string.conta_saiu, Toast.LENGTH_SHORT).show()
                // NEW_TASK + CLEAR_TASK apagam todas as telas abertas e deixam só o Login.
                // Assim o botão Voltar do celular não volta para dentro do app.
                activity.startActivity(
                    Intent(activity, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(stringResource(R.string.conta_botao_sair), fontSize = 16.sp)
        }
    }
}
