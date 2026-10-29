package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = Purple900,
  onPrimary = Color.White,
  primaryContainer = Purple200,
  onPrimaryContainer = Purple950,
  secondary = Purple700,
  onSecondary = Color.White,
  secondaryContainer = Purple50,
  onSecondaryContainer = Purple900,
  tertiary = Amber500,
  onTertiary = Slate950,
  background = Slate50,
  onBackground = Slate900,
  surface = Color.White,
  onSurface = Slate900,
  surfaceVariant = Slate100,
  onSurfaceVariant = Slate700,
  outline = Slate300
)

private val DarkColorScheme = darkColorScheme(
  primary = Purple500,
  onPrimary = Slate950,
  primaryContainer = Purple950,
  onPrimaryContainer = Purple200,
  secondary = Amber400,
  onSecondary = Slate950,
  background = Slate950,
  onBackground = Slate50,
  surface = Slate900,
  onSurface = Slate50,
  surfaceVariant = Slate800,
  onSurfaceVariant = Slate300,
  outline = Slate700
)

@Composable
fun H2sStudyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      WindowCompat.setDecorFitsSystemWindows(window, false)
      window.statusBarColor = Color.Transparent.toArgb()
      window.navigationBarColor = Color.Transparent.toArgb()
      val insetsController = WindowCompat.getInsetsController(window, view)
      insetsController.isAppearanceLightStatusBars = !darkTheme
      insetsController.isAppearanceLightNavigationBars = !darkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
