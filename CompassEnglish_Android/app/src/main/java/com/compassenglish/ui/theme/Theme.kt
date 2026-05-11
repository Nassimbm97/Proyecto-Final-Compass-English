package com.compassenglish.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val GoldPrimary     = Color(0xFFD4A843)
val GoldDark        = Color(0xFFB8891E)
val GoldLight       = Color(0xFFEDC96A)
val GoldSurface     = Color(0xFFF5EDD8)
val GoldSurfaceCard = Color(0xFFFBF6EE)
val GoldBorder      = Color(0xFFE8C97A)
val TextDark        = Color(0xFF3D2B0E)
val TextMedium      = Color(0xFF7A5C2E)
val TextLight       = Color(0xFFB09060)
val ErrorRed        = Color(0xFFE03E3E)
val SuccessGreen    = Color(0xFF4CAF50)
val White           = Color(0xFFFFFFFF)

private val CompassColorScheme = lightColorScheme(
    primary         = GoldPrimary,
    onPrimary       = White,
    secondary       = GoldDark,
    onSecondary     = White,
    background      = GoldSurface,
    onBackground    = TextDark,
    surface         = GoldSurfaceCard,
    onSurface       = TextDark,
    error           = ErrorRed,
)

@Composable
fun CompassEnglishTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CompassColorScheme,
        content = content
    )
}
