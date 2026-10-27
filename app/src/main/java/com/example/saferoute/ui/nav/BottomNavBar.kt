package com.example.saferoute.ui.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val destino = navBackStackEntry?.destination
        items.forEach { item ->
            val titulo = stringResource(item.titulo)
            NavigationBarItem(
                icon = { Icon(imageVector = item.icone, contentDescription = titulo) },
                label = { Text(text = titulo) },
                alwaysShowLabel = true,
                // Aba destacada = a rota da tela aberta é a rota deste item
                selected = destino?.hierarchy?.any { it.hasRoute(item.rota::class) } == true,
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
