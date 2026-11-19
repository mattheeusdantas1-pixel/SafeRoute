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
import com.example.saferoute.ui.EmailField
import com.example.saferoute.ui.MensagemErro
import com.example.saferoute.ui.PasswordField
import com.example.saferoute.ui.coresBotaoPositivo
import com.example.saferoute.ui.theme.SafeRouteTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeRouteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    // rememberSaveable: o que foi digitado sobrevive se a tela girar
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }
    var carregando by remember { mutableStateOf(false) }
    val activity = LocalActivity.current as Activity

    val fieldModifier = Modifier.fillMaxWidth()
    val podeEntrar = email.isNotEmpty() && senha.isNotEmpty()

    // imePadding + rolagem: com o teclado aberto os botões continuam ao alcance
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ainda não existe arquivo do logo, então por enquanto o nome faz esse papel
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.login_titulo),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(24.dp))
        EmailField(
            value = email,
            onValueChange = { email = it; erro = null },
            modifier = fieldModifier
        )
        Spacer(modifier = Modifier.size(12.dp))
        PasswordField(
            label = stringResource(R.string.campo_senha),
            value = senha,
            onValueChange = { senha = it; erro = null },
            modifier = fieldModifier
        )
        MensagemErro(erro, fieldModifier)
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = {
                erro = validarEmail(email)
                if (erro == null) {
                    carregando = true
                    Firebase.auth.signInWithEmailAndPassword(email.trim(), senha)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, R.string.login_ok, Toast.LENGTH_LONG).show()
                                activity.startActivity(
                                    Intent(activity, MainActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                )
                                activity.finish()
                            } else {
                                carregando = false
                                // Uma mensagem só, para não revelar se o e-mail existe
                                erro = when (task.exception) {
                                    is FirebaseNetworkException -> R.string.conta_erro_rede
                                    is FirebaseAuthInvalidUserException,
                                    is FirebaseAuthInvalidCredentialsException -> R.string.conta_erro_login
                                    else -> R.string.conta_erro_generico
                                }
                            }
                        }
                }
            },
            enabled = podeEntrar && !carregando,
            modifier = fieldModifier.height(56.dp),
            colors = coresBotaoPositivo()
        ) {
            CadeadoDesabilitado(podeEntrar && !carregando)
            Text(stringResource(R.string.botao_entrar), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row(
            modifier = fieldModifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotaoContorno(
                onClick = { if (!carregando) { email = ""; senha = ""; erro = null } },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text(stringResource(R.string.botao_limpar), style = MaterialTheme.typography.titleMedium)
            }
            BotaoContorno(
                onClick = {
                    if (!carregando) {
                        activity.startActivity(Intent(activity, RegisterActivity::class.java))
                    }
                },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text(stringResource(R.string.botao_registrar), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
