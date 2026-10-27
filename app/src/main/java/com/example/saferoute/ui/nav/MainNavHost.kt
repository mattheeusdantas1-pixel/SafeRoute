package com.example.saferoute.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.saferoute.MainViewModel
import com.example.saferoute.ui.ContatosPage
import com.example.saferoute.ui.HistoricoPage
import com.example.saferoute.ui.InicioPage
import com.example.saferoute.ui.PerfilPage
import com.example.saferoute.ui.SosPage
import com.example.saferoute.ui.TrajetoPage

// As telas não recebem o navController: recebem só funções (onSosClick, onVoltar).
// Assim a tela não precisa saber como a navegação funciona.
@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val abrirSos = { navController.navigate(Route.Sos) }

    NavHost(navController, startDestination = Route.Inicio) {
        composable<Route.Inicio> {
            InicioPage(modifier = modifier, viewModel = viewModel, onSosClick = abrirSos)
        }
        composable<Route.Trajeto> { TrajetoPage(modifier = modifier, viewModel = viewModel) }
        composable<Route.Contatos> { ContatosPage(modifier = modifier, viewModel = viewModel) }
        composable<Route.Historico> { HistoricoPage(modifier = modifier, viewModel = viewModel) }
        composable<Route.Perfil> { PerfilPage(modifier = modifier, viewModel = viewModel) }
        composable<Route.Sos> {
            SosPage(
                modifier = modifier,
                viewModel = viewModel,
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
