package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class StatusColors(
  val success: Color,
  val warning: Color,
  val error: Color,
  val neutral: Color,
)

internal val LightStatusColors = StatusColors(
  success = Color(0xFF2B8A3E),
  warning = Color(0xFFE67700),
  error = Color(0xFFC0392B),
  neutral = Color(0xFF767781),
)

internal val DarkStatusColors = StatusColors(
  success = Color(0xFF69DB7C),
  warning = Color(0xFFFFC078),
  error = Color(0xFFFF897D),
  neutral = Color(0xFF90919B),
)

val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }
