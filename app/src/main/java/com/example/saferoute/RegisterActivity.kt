package com.example.saferoute

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saferoute.ui.DataField
import com.example.saferoute.ui.MensagemErro
import com.example.saferoute.ui.PasswordField
import com.example.saferoute.ui.TelefoneField
import com.example.saferoute.ui.theme.SafeRouteTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeRouteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var nome by rememberSaveable { mutableStateOf("") }
    var telefone by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmacao by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }
    val activity = LocalActivity.current as Activity

    val fieldModifier = Modifier.fillMaxWidth(fraction = 0.9f)
    // Aviso na hora, enquanto digita a confirmação (o botão fica desabilitado nesse caso)
    val senhasDiferentes = confirmacao.isNotEmpty() && senha != confirmacao

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.cadastro_titulo),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.size(24.dp))
        DataField(
            label = stringResource(R.string.campo_nome),
            value = nome,
            onValueChange = { nome = it; erro = null },
            modifier = fieldModifier
        )
        Spacer(modifier = Modifier.size(12.dp))
        TelefoneField(
            value = telefone,
            onValueChange = { telefone = it; erro = null },
            modifier = fieldModifier
        )
        Spacer(modifier = Modifier.size(12.dp))
        PasswordField(
            label = stringResource(R.string.campo_senha),
            value = senha,
            onValueChange = { senha = it; erro = null },
            modifier = fieldModifier
        )
        Spacer(modifier = Modifier.size(12.dp))
        PasswordField(
            label = stringResource(R.string.campo_confirmar_senha),
            value = confirmacao,
            onValueChange = { confirmacao = it; erro = null },
            modifier = fieldModifier
        )
        MensagemErro(if (senhasDiferentes) R.string.erro_senhas_diferentes else erro)
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    erro = validarCadastro(nome, telefone, senha, confirmacao)
                    if (erro == null) {
                        Toast.makeText(activity, R.string.cadastro_ok, Toast.LENGTH_LONG).show()
                        // Cadastrou, já entra no app com o nome e o telefone
                        activity.startActivity(
                            Intent(activity, MainActivity::class.java)
                                .putExtra(EXTRA_NOME, nome.trim())
                                .putExtra(EXTRA_TELEFONE, telefone)
                                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        )
                        activity.finish()
                    }
                },
                enabled = nome.isNotEmpty() && telefone.isNotEmpty() &&
                          senha.isNotEmpty() && confirmacao.isNotEmpty() &&
                          senha == confirmacao
            ) {
                Text(stringResource(R.string.botao_confirmar))
            }
            Button(
                onClick = {
                    nome = ""; telefone = ""; senha = ""; confirmacao = ""; erro = null
                }
            ) {
                Text(stringResource(R.string.botao_limpar))
            }
        }
    }
}
