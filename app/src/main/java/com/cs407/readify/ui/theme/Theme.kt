package com.cs407.readify.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Red,                 // Accent color for buttons or actions
    onPrimary = White,              // Text color on primary color (Red)
    primaryContainer = SurfaceGray, // Background for cards and containers
    onPrimaryContainer = White,     // Text color on container backgrounds
    background = Black,             // Background color for the whole app
    surface = SurfaceGray,          // Surface color for cards/containers
    onBackground = White,           // Text color on background
    onSurface = White               // Text color on surface (cards/containers)
)

private val LightColorScheme = lightColorScheme(
    primary = Red,                 // Accent color remains consistent
    onPrimary = White,              // Text on primary color (Red)
    primaryContainer = LightGray,   // Background for cards and containers in light mode
    onPrimaryContainer = DarkGray,  // Text color on container backgrounds in light mode
    background = White,             // Background color for the whole app in light mode
    surface = LightGray,            // Surface color for cards/containers in light mode
    onBackground = DarkGray,        // Text color on background in light mode
    onSurface = DarkGray            // Text color on surface in light mode
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun ReadifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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