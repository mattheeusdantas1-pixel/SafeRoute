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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel by viewModels()

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
