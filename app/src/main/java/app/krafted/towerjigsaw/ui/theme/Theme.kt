package app.krafted.towerjigsaw.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GameColorScheme = darkColorScheme(
    primary = Color(0xFFFFD54F),
    onPrimary = Color(0xFF1A1A2E),
    primaryContainer = Color(0xFF3D3200),
    secondary = Color(0xFF9090B0),
    onSecondary = Color(0xFF1A1A2E),
    background = Color(0xFF080810),
    onBackground = Color(0xFFF0F0F8),
    surface = Color(0xFF13132B),
    onSurface = Color(0xFFF0F0F8),
    surfaceVariant = Color(0xFF1E1E40),
    onSurfaceVariant = Color(0xFF9090B0),
    outline = Color(0xFF252548)
)

@Composable
fun TowerJigsawTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}
