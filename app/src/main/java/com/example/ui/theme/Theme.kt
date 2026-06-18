package com.example.ui.theme

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
    primary = GoldenAmber,
    secondary = WarmAsh,
    onSecondary = PaperWhite,
    tertiary = GoldMuted,
    background = CharcoalDark,
    surface = WarmAsh,
    onPrimary = SolidBlack,
    onBackground = PaperWhite,
    onSurface = PaperWhite,
    surfaceVariant = SolidBlack,
    onSurfaceVariant = InkMuted
)

private val LightColorScheme = lightColorScheme(
    primary = DeepEspresso,
    secondary = WarmSand,
    onSecondary = DeepEspresso,
    tertiary = BookGold,
    background = IvoryBackground,
    surface = PaperWhite,
    onPrimary = PaperWhite,
    onBackground = DeepEspresso,
    onSurface = DeepEspresso,
    surfaceVariant = WarmSand,
    onSurfaceVariant = DeepEspresso
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to support our custom editorial aesthetic
    content: @Composable () -> Unit,
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
