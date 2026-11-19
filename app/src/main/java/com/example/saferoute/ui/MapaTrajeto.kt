package com.example.saferoute.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.saferoute.model.Localizacao
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

// Mapa reaproveitado nas telas de Trajeto e SOS.
// Se receber uma localização, mostra um marcador nela e leva a câmera até lá.
@Composable
fun MapaTrajeto(
    modifier: Modifier = Modifier,
    localizacao: Localizacao? = null
) {
    val context = LocalContext.current

    // O mapa em si não precisa de permissão. Ela só serve para o ponto azul da posição da pessoa
    var temPermissao by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val pedirPermissao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida -> temPermissao = concedida }

    LaunchedEffect(Unit) {
        if (!temPermissao) pedirPermissao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Começa em Recife. rememberCameraPositionState guarda a câmera quando a tela é redesenhada
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-8.05, -34.9), 13f)
    }

    // Quando chega uma localização nova (o SOS reenvia a cada minuto), a câmera vai até ela
    LaunchedEffect(localizacao) {
        localizacao?.let {
            camera.animate(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camera,
        properties = MapProperties(isMyLocationEnabled = temPermissao),
        uiSettings = MapUiSettings(myLocationButtonEnabled = temPermissao)
    ) {
        localizacao?.let {
            Marker(
                state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                title = "SOS",
                snippet = it.endereco
            )
        }
    }
}
