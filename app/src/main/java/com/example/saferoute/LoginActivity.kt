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
import com.example.saferoute.ui.MensagemErro
import com.example.saferoute.ui.PasswordField
import com.example.saferoute.ui.TelefoneField
import com.example.saferoute.ui.theme.SafeRouteTheme

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
    var telefone by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<Int?>(null) }
    val activity = LocalActivity.current as Activity

    val fieldModifier = Modifier.fillMaxWidth(fraction = 0.9f)

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_titulo),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.size(24.dp))
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
        MensagemErro(erro)
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    // Login simulado: só confere o telefone, não existe servidor ainda
                    erro = validarTelefone(telefone)
                    if (erro == null) {
                        Toast.makeText(activity, R.string.login_ok, Toast.LENGTH_LONG).show()
                        activity.startActivity(
                            Intent(activity, MainActivity::class.java)
                                .putExtra(EXTRA_TELEFONE, telefone)
                                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        )
                    }
                },
                enabled = telefone.isNotEmpty() && senha.isNotEmpty()
            ) {
                Text(stringResource(R.string.botao_entrar))
            }
            Button(
                onClick = { telefone = ""; senha = ""; erro = null }
            ) {
                Text(stringResource(R.string.botao_limpar))
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        Button(
            onClick = {
                activity.startActivity(Intent(activity, RegisterActivity::class.java))
            }
        ) {
            Text(stringResource(R.string.botao_registrar))
        }
    }
}
