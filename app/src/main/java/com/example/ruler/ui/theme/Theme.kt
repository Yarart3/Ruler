package com.example.ruler.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Blue700,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Blue500,
    onSecondary = Blue900,
    background = Grey100,
    onBackground = Blue900,
    surface = White,
    onSurface = Grey900,
    surfaceVariant = Blue100,
    onSurfaceVariant = Grey700,
    error = ErrorRed,
    onError = White,
)

private val DarkColors = darkColorScheme(
    primary = Blue500,
    onPrimary = Blue900,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Blue700,
    onSecondary = White,
    background = Grey900,
    onBackground = Grey100,
    surface = Color(0xFF3A4A5C),
    onSurface = Grey100,
    surfaceVariant = Grey700,
    onSurfaceVariant = Grey300,
    error = ErrorRed,
    onError = White,
)

@Composable
fun RulerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}