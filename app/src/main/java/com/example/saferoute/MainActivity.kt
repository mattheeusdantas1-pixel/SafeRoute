package com.example.saferoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saferoute.ui.nav.BottomNavBar
import com.example.saferoute.ui.nav.BottomNavItem
import com.example.saferoute.ui.nav.MainNavHost
import com.example.saferoute.ui.nav.Route
import com.example.saferoute.ui.theme.SafeRouteTheme

// Nomes dos dados que o Login e o Cadastro mandam pelo Intent
const val EXTRA_NOME = "nome"
const val EXTRA_TELEFONE = "telefone"

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: MainViewModel by viewModels()

        // Só na primeira abertura. Se a tela girar, o ViewModel já tem os dados
        // (e o nome pode ter sido editado no Perfil, não dá para sobrescrever).
        if (savedInstanceState == null) {
            val telefone = intent.getStringExtra(EXTRA_TELEFONE)
            if (telefone != null) {
                // O login simulado não sabe o nome (não tem servidor); aí fica o nome de exemplo
                val nome = intent.getStringExtra(EXTRA_NOME) ?: viewModel.usuario.nome
                viewModel.definirUsuario(nome, telefone)
            }
        }

        setContent {
            val navController = rememberNavController()

            // Na tela de SOS a barra inferior some, para a tela ficar só com o socorro
            val entradaAtual by navController.currentBackStackEntryAsState()
            val naTelaSos = entradaAtual?.destination?.hasRoute(Route.Sos::class) == true

            SafeRouteTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(stringResource(R.string.app_name)) })
                    },
                    bottomBar = {
                        if (!naTelaSos) {
                            val items = listOf(
                                BottomNavItem.InicioButton,
                                BottomNavItem.TrajetoButton,
                                BottomNavItem.ContatosButton,
                                BottomNavItem.HistoricoButton,
                                BottomNavItem.PerfilButton,
                            )
                            BottomNavBar(navController = navController, items)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainNavHost(navController = navController, viewModel = viewModel)
                    }
                }
            }
        }
    }
}
