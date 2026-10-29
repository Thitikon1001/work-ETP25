package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WearthColorScheme = lightColorScheme(
  primary = WearthGreen,
  onPrimary = Color.White,
  primaryContainer = WearthLightTeal,
  onPrimaryContainer = WearthDarkGreen,
  secondary = WearthTealGreen,
  onSecondary = Color.White,
  secondaryContainer = WearthDepositBg,
  onSecondaryContainer = WearthDarkGreen,
  tertiary = WearthPointsGold,
  onTertiary = Color.White,
  tertiaryContainer = WearthPointsGoldBg,
  onTertiaryContainer = WearthPointsGoldText,
  background = WearthBackground,
  onBackground = WearthTextPrimary,
  surface = WearthSurface,
  onSurface = WearthTextPrimary,
  surfaceVariant = Color(0xFFF0F4F1),
  onSurfaceVariant = WearthTextSecondary,
  outline = WearthBorder,
  error = WearthRed,
  onError = Color.White,
  errorContainer = WearthRedBg,
  onErrorContainer = WearthRed
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent WEARTH green eco branding
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = WearthColorScheme,
    typography = WearthTypography,
    content = content
  )
}
