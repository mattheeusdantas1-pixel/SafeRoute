package com.example.saferoute.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.saferoute.R

// O título é o id do texto no strings.xml (R.string...), e não o texto direto
sealed class BottomNavItem(
    @StringRes val titulo: Int,
    val icone: ImageVector,
    val rota: Route
) {
    data object InicioButton :
        BottomNavItem(R.string.aba_inicio, Icons.Default.Home, Route.Inicio)
    data object TrajetoButton :
        BottomNavItem(R.string.aba_trajeto, Icons.Default.Route, Route.Trajeto)
    data object ContatosButton :
        BottomNavItem(R.string.aba_contatos, Icons.Default.Group, Route.Contatos)
    data object HistoricoButton :
        BottomNavItem(R.string.aba_historico, Icons.Default.History, Route.Historico)
    data object PerfilButton :
        BottomNavItem(R.string.aba_perfil, Icons.Default.Person, Route.Perfil)
}
