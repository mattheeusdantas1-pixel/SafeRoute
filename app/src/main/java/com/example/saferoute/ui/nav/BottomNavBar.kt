package com.example.saferoute.ui.nav

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saferoute.ui.theme.NoitePetroleo
import com.example.saferoute.ui.theme.VerdeAgua
import com.example.saferoute.ui.theme.VerdeAguaProfundo

@Composable
fun BottomNavBar(navController: NavHostController, items: List<BottomNavItem>) {
    // Sobre a barra branca do modo claro, o Verde-água puro não dá contraste para texto
    val corTextoSelecionado = if (isSystemInDarkTheme()) VerdeAgua else VerdeAguaProfundo
    val corNormal = MaterialTheme.colorScheme.onSurfaceVariant

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val destino = navBackStackEntry?.destination
        items.forEach { item ->
            val titulo = stringResource(item.titulo)
            // Aba destacada = a rota da tela aberta é a rota deste item
            val selecionado = destino?.hierarchy?.any { it.hasRoute(item.rota::class) } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selecionado) item.iconeSelecionado else item.icone,
                        contentDescription = titulo
                    )
                },
                label = {
                    Text(
                        text = titulo,
                        fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true,
                selected = selecionado,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NoitePetroleo,
                    selectedTextColor = corTextoSelecionado,
                    indicatorColor = VerdeAgua,
                    unselectedIconColor = corNormal,
                    unselectedTextColor = corNormal
                ),
                onClick = {
                    navController.navigate(item.rota) {
                        // Volta até o Início antes de abrir a aba, para não empilhar telas sem fim
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
