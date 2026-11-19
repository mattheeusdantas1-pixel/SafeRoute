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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.saferoute.ui.BotaoContorno
import com.example.saferoute.ui.CadeadoDesabilitado
import com.example.saferoute.ui.DataField
import com.example.saferoute.ui.EmailField
import com.example.saferoute.ui.MensagemErro
import com.example.saferoute.ui.PasswordField
import com.example.saferoute.ui.TelefoneField
import com.example.saferoute.ui.coresBotaoPositivo
import com.example.saferoute.ui.theme.SafeRouteTheme
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

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
    var email by rememberSaveable { mutableStateOf("") }
    var telefone by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmacao by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }
    var carregando by remember { mutableStateOf(false) }
    val activity = LocalActivity.current as Activity

    val fieldModifier = Modifier.fillMaxWidth()
    // Aviso na hora, enquanto digita a confirmação (o botão fica desabilitado nesse caso)
    val senhasDiferentes = confirmacao.isNotEmpty() && senha != confirmacao
    val podeConfirmar = nome.isNotEmpty() && email.isNotEmpty() && telefone.isNotEmpty() &&
                        senha.isNotEmpty() && confirmacao.isNotEmpty() &&
                        senha == confirmacao

    // imePadding + rolagem: são 4 campos, e com o teclado aberto eles não cabem todos na tela
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.cadastro_titulo),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.size(24.dp))
        DataField(
            label = stringResource(R.string.campo_nome),
            value = nome,
            onValueChange = { nome = it; erro = null },
            modifier = fieldModifier
        )
        Spacer(modifier = Modifier.size(12.dp))
        EmailField(
            value = email,
            onValueChange = { email = it; erro = null },
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
        MensagemErro(if (senhasDiferentes) R.string.erro_senhas_diferentes else erro, fieldModifier)
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = {
                erro = validarCadastro(nome, email, telefone, senha, confirmacao)
                if (erro == null) {
                    carregando = true
                    Firebase.auth.createUserWithEmailAndPassword(email.trim(), senha)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                // Guarda o nome no perfil do Firebase e só depois entra no app
                                val perfil = userProfileChangeRequest { displayName = nome.trim() }
                                task.result.user?.updateProfile(perfil)
                                    ?.addOnCompleteListener(activity) {
                                        Toast.makeText(activity, R.string.cadastro_ok, Toast.LENGTH_LONG).show()
                                        activity.startActivity(
                                            Intent(activity, MainActivity::class.java)
                                                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                        )
                                        activity.finish()
                                    }
                            } else {
                                carregando = false
                                erro = when (task.exception) {
                                    is FirebaseAuthUserCollisionException -> R.string.conta_erro_email_existe
                                    is FirebaseNetworkException -> R.string.conta_erro_rede
                                    else -> R.string.conta_erro_generico
                                }
                            }
                        }
                }
            },
            enabled = podeConfirmar && !carregando,
            modifier = fieldModifier.height(56.dp),
            colors = coresBotaoPositivo()
        ) {
            CadeadoDesabilitado(podeConfirmar && !carregando)
            Text(stringResource(R.string.botao_confirmar), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.size(12.dp))
        BotaoContorno(
            onClick = {
                // Durante o cadastro não limpa, senão some o que está sendo enviado
                if (!carregando) {
                    nome = ""; email = ""; telefone = ""; senha = ""; confirmacao = ""; erro = null
                }
            },
            modifier = fieldModifier.height(56.dp)
        ) {
            Text(stringResource(R.string.botao_limpar), style = MaterialTheme.typography.titleMedium)
        }
    }
}
