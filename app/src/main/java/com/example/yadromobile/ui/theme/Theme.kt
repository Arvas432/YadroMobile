package com.example.yadromobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = black14,
    secondary = grayDark,
    tertiary = blueE7,
    onPrimary = whiteFD,
    onSecondary = whiteFD,
    onTertiary = whiteFD
)

private val LightColorScheme = lightColorScheme(
    primary = whiteFD,
    secondary = grayEB,
    tertiary = blueE7,
    onPrimary = black14,
    onSecondary = black14,
    onTertiary = whiteFD
)

@Composable
fun YadroMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}