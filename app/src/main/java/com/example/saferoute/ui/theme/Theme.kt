package com.example.saferoute.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Modo escuro: Noite Petróleo no fundo, Verde-água nas ações, Âmbar para atenção.
private val DarkColorScheme = darkColorScheme(
    primary = VerdeAgua,
    onPrimary = NoitePetroleo,
    primaryContainer = Petroleo,
    onPrimaryContainer = Nevoa,
    secondary = AguaAcinzentada,
    onSecondary = NoitePetroleo,
    secondaryContainer = PetroleoEscuro,
    onSecondaryContainer = Nevoa,
    tertiary = Ambar,
    onTertiary = NoitePetroleo,
    tertiaryContainer = AmbarProfundo,
    onTertiaryContainer = AmbarClaro,
    background = NoitePetroleo,
    onBackground = Nevoa,
    surface = NoitePetroleo,
    onSurface = Nevoa,
    surfaceVariant = Petroleo,
    onSurfaceVariant = NevoaSuave,
    // Card, barra inferior e diálogos usam estes tons. Sem definir, viriam no cinza-lilás padrão do Material.
    surfaceContainerLowest = NoitePetroleo,
    surfaceContainerLow = NoitePetroleo,
    surfaceContainer = PetroleoEscuro,
    surfaceContainerHigh = PetroleoEscuro,
    surfaceContainerHighest = Petroleo,
    outline = VerdeAguaProfundo,
    outlineVariant = PetroleoEscuro,
    error = SosVermelhoClaro,
    onError = NoitePetroleo
)

// Modo claro: Névoa no fundo, Petróleo nas ações.
private val LightColorScheme = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = AguaClara,
    onPrimaryContainer = NoitePetroleo,
    secondary = VerdeAguaProfundo,
    onSecondary = Color.White,
    secondaryContainer = NevoaSuave,
    onSecondaryContainer = NoitePetroleo,
    tertiary = AmbarProfundo,
    onTertiary = Color.White,
    tertiaryContainer = AmbarClaro,
    onTertiaryContainer = AmbarProfundo,
    background = Nevoa,
    onBackground = NoitePetroleo,
    surface = Color.White,
    onSurface = NoitePetroleo,
    surfaceVariant = NevoaSuave,
    onSurfaceVariant = AcinzentadoEscuro,
    // Mesmo motivo do modo escuro: cards, barra e diálogos brancos sobre o fundo Névoa.
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    outline = Petroleo,
    outlineVariant = NevoaSuave,
    error = SosVermelho,
    onError = Color.White
)

// Cantos arredondados em tudo (retângulo arredondado = acolhedor, conforme a psicologia das formas)
private val FormasSafeRoute = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SafeRouteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = FormasSafeRoute,
        content = content
    )
}
