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

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = VibrantPrimaryDark,
    onPrimary = VibrantOnPrimaryDark,
    primaryContainer = VibrantPrimaryContainerDark,
    onPrimaryContainer = VibrantOnPrimaryContainerDark,
    secondary = VibrantSecondaryDark,
    onSecondary = VibrantOnSecondaryDark,
    secondaryContainer = VibrantSecondaryContainerDark,
    onSecondaryContainer = VibrantOnSecondaryContainerDark,
    tertiary = VibrantTertiaryDark,
    onTertiary = VibrantOnTertiaryDark,
    tertiaryContainer = VibrantTertiaryContainerDark,
    onTertiaryContainer = VibrantOnTertiaryContainerDark,
    background = VibrantBackgroundDark,
    onBackground = VibrantOnBackgroundDark,
    surface = VibrantSurfaceDark,
    onSurface = VibrantOnSurfaceDark,
    outline = VibrantOutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantPrimary,
    onPrimary = VibrantOnPrimary,
    primaryContainer = VibrantPrimaryContainer,
    onPrimaryContainer = VibrantOnPrimaryContainer,
    secondary = VibrantSecondary,
    onSecondary = VibrantOnSecondary,
    secondaryContainer = VibrantSecondaryContainer,
    onSecondaryContainer = VibrantOnSecondaryContainer,
    tertiary = VibrantTertiary,
    onTertiary = VibrantOnTertiary,
    tertiaryContainer = VibrantTertiaryContainer,
    onTertiaryContainer = VibrantOnTertiaryContainer,
    background = VibrantBackground,
    onBackground = VibrantOnBackground,
    surface = VibrantSurface,
    onSurface = VibrantOnSurface,
    outline = VibrantOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Override to false by default so our custom Vibrant Palette is shown
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
