package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PromptFontFamily = FontFamily(
  Font(R.font.prompt, FontWeight.Normal)
)

val WearthTypography = Typography(
  displayLarge = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    color = WearthTextPrimary
  ),
  headlineLarge = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    color = WearthTextPrimary
  ),
  headlineMedium = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    color = WearthTextPrimary
  ),
  titleLarge = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    color = WearthTextPrimary
  ),
  titleMedium = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    color = WearthTextPrimary
  ),
  titleSmall = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = WearthTextPrimary
  ),
  bodyLarge = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    color = WearthTextPrimary
  ),
  bodyMedium = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = WearthTextSecondary
  ),
  bodySmall = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = WearthTextSecondary
  ),
  labelLarge = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 20.sp
  ),
  labelMedium = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp
  ),
  labelSmall = TextStyle(
    fontFamily = PromptFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 14.sp
  )
)
