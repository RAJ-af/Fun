package com.itsraj.funkytalk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MangoYellow,
    onPrimary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = GrayLight,
    onSurfaceVariant = Black,
    outline = Black
)

@Composable
fun FunkyTalkTheme(
    darkTheme: Boolean = false, // Strictly Light Mode as requested
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We use LightColorScheme for both to ensure strictly light mode for now
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
