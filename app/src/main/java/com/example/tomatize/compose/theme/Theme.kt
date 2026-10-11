package com.example.tomatize.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NavActive,
    onPrimary = White,
    secondary = GoodHabitColor,
    onSecondary = White,
    error = RedColor,
    onError = White,
    background = BackgroundLight,
    onBackground = Black,
    surface = WhiteCard,
    onSurface = Black
)

@Composable
fun TomatizeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
