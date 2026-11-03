package com.example.saferoute.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Route
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.saferoute.R

// O título é o id do texto no strings.xml (R.string...), e não o texto direto.
// Dois ícones: contornado na aba normal e preenchido na aba aberta, para a seleção não depender só de cor.
sealed class BottomNavItem(
    @StringRes val titulo: Int,
    val icone: ImageVector,
    val iconeSelecionado: ImageVector,
    val rota: Route
) {
    data object InicioButton :
        BottomNavItem(R.string.aba_inicio, Icons.Outlined.Home, Icons.Filled.Home, Route.Inicio)
    data object TrajetoButton :
        BottomNavItem(R.string.aba_trajeto, Icons.Outlined.Route, Icons.Filled.Route, Route.Trajeto)
    data object ContatosButton :
        BottomNavItem(R.string.aba_contatos, Icons.Outlined.Group, Icons.Filled.Group, Route.Contatos)
    data object HistoricoButton :
        BottomNavItem(R.string.aba_historico, Icons.Outlined.History, Icons.Filled.History, Route.Historico)
    data object PerfilButton :
        BottomNavItem(R.string.aba_perfil, Icons.Outlined.Person, Icons.Filled.Person, Route.Perfil)
}
