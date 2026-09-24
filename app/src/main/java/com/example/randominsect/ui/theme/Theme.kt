package com.example.randominsect.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrightLeafGreen,
    onPrimary = DarkGreenBackground,
    primaryContainer = ForestGreen,
    onPrimaryContainer = LightGreen,
    secondary = EarthGreenLight,
    onSecondary = DarkOliveBackground,
    secondaryContainer = OliveContainer,
    onSecondaryContainer = LightOlive,
    tertiary = BrightAmber,
    onTertiary = DarkAmberBackground,
    tertiaryContainer = BrownContainer,
    onTertiaryContainer = AmberContainer,
    background = DarkBackground,
    onBackground = LightEarthText,
    surface = DarkBackground,
    onSurface = LightEarthText,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = LeafGreen,
    onPrimary = LightBackground, // Or White
    primaryContainer = LightGreen,
    onPrimaryContainer = DarkGreenText,
    secondary = EarthBrown,
    onSecondary = LightBackground,
    secondaryContainer = LightOlive,
    onSecondaryContainer = DarkOliveText,
    tertiary = AmberAccent,
    onTertiary = LightBackground,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = DarkAmberText,
    background = LightBackground,
    onBackground = DarkEarthText,
    surface = LightBackground,
    onSurface = DarkEarthText,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

@Composable
fun RandomInsectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Let's disable dynamic colors to enforce the insect theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
