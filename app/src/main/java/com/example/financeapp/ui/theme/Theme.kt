package com.example.financeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = Color(0xFF84B082),      // Sage Green from Clancy
    secondary = Color(0xFF1E2D2F),    // Dark Steel Blue from Clancy
    tertiary = Color(0xFFCC4429),     // Rust Red from Clancy
    background = Color(0xFF121212),   // Dark Background
    surface = Color(0xFF1E1E1E),      // Dark Surface
    error = Color(0xFFCF6679),        // Error Red
    primaryContainer = Color(0xFF6B8E69),  // Darker Sage Green
    secondaryContainer = Color(0xFF364B54), // Lighter Steel Blue
    tertiaryContainer = Color(0xFFA33621),  // Darker Rust Red
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black,
    surfaceVariant = Color(0xFF2C2C2C)  // Slightly lighter than surface
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF84B082),      // Sage Green from Clancy
    secondary = Color(0xFFFF5733),    // Bright Orange from Clancy
    tertiary = Color(0xFFCC4429),     // Rust Red from Clancy
    background = Color.White,
    surface = Color(0xFFF5F5F5),      // Light Gray
    error = Color(0xFFB00020),        // Error Red
    primaryContainer = Color(0xFFB7D4B5),  // Lighter Sage Green
    secondaryContainer = Color(0xFFFFE0D6), // Light Orange
    tertiaryContainer = Color(0xFFFFDAD6),  // Light Rust Red
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
    surfaceVariant = Color(0xFFE1E1E1)  // Slightly darker than surface
)

@Composable
fun FinanceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 