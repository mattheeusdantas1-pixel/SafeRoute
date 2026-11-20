package com.example.saferoute.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.saferoute.R
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
    localizacao: Localizacao? = null,
    destino: Localizacao? = null,
    descDestino: String? = null,
    aoSegurar: ((LatLng) -> Unit)? = null
) {
    val context = LocalContext.current
    // Marker não é @Composable de texto, então o texto é lido aqui fora
    val tituloDestino = stringResource(R.string.lugar_marcador_destino)

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

    // Quando o destino muda (digitado, sugerido ou escolhido no mapa), a câmera vai até ele
    LaunchedEffect(destino) {
        destino?.let {
            camera.animate(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camera,
        properties = MapProperties(isMyLocationEnabled = temPermissao),
        uiSettings = MapUiSettings(myLocationButtonEnabled = temPermissao),
        onMapLongClick = { aoSegurar?.invoke(it) }
    ) {
        localizacao?.let {
            Marker(
                state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                title = "SOS",
                snippet = it.endereco
            )
        }
        destino?.let {
            // key faz um estado novo de marcador a cada destino, senão ele ficaria na posição antiga
            key(it.latitude, it.longitude) {
                Marker(
                    state = rememberMarkerState(position = LatLng(it.latitude, it.longitude)),
                    title = tituloDestino,
                    // Com o clima já carregado, o balão mostra o clima no lugar do endereço
                    snippet = descDestino ?: it.endereco
                )
            }
        }
    }
}
