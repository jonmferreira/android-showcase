package com.jonathan.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppNav()
            }
        }
    }
}

// Paleta de marca (verde + azul) — dá identidade e tira o look "default".
private val LightColors = lightColorScheme(
    primary = Color(0xFF1F8F55),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBFE9CF),
    onPrimaryContainer = Color(0xFF06331C),
    secondary = Color(0xFF2563EB),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7E3FF),
    background = Color(0xFFF5F8F6),
    onBackground = Color(0xFF111714),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE6EDE8),
    onSurfaceVariant = Color(0xFF53605A),
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF59E0A0),
    onPrimary = Color(0xFF00351C),
    primaryContainer = Color(0xFF1F8F55),
    onPrimaryContainer = Color(0xFFBFE9CF),
    secondary = Color(0xFF86B0FF),
    onSecondary = Color(0xFF0A2A66),
    background = Color(0xFF0E1116),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF161B22),
    surfaceVariant = Color(0xFF232A33),
    onSurfaceVariant = Color(0xFF9FB0BD),
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
