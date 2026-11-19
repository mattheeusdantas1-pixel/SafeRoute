package com.example.saferoute

import android.content.Intent
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
import com.example.saferoute.db.fb.FBDatabase
import com.example.saferoute.ui.nav.BottomNavBar
import com.example.saferoute.ui.nav.BottomNavItem
import com.example.saferoute.ui.nav.MainNavHost
import com.example.saferoute.ui.nav.Route
import com.example.saferoute.ui.theme.SafeRouteTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    // Se a sessão acabar (Sair), volta para o Login e apaga as telas abertas
    private val ouvinteSessao = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Firebase.auth.addAuthStateListener(ouvinteSessao)
    }

    override fun onStop() {
        super.onStop()
        Firebase.auth.removeAuthStateListener(ouvinteSessao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: MainViewModel by viewModels { MainViewModelFactory(FBDatabase()) }

        // Só na primeira abertura. Se a tela girar, o ViewModel já tem os dados
        // (e o nome pode ter sido editado no Perfil, não dá para sobrescrever).
        if (savedInstanceState == null) {
            val usuarioLogado = Firebase.auth.currentUser
            if (usuarioLogado != null) {
                // Conta sem nome no perfil: mantém o nome que o ViewModel já tem
                val nome = usuarioLogado.displayName?.takeIf { it.isNotBlank() }
                    ?: viewModel.usuario.nome
                viewModel.definirUsuario(
                    nome = nome,
                    telefone = "",   // o telefone só vem do banco na Prática 6
                    email = usuarioLogado.email ?: ""
                )
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
