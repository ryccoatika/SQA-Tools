package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class FeatureAccent(
  val light: Color,
  val lightEnd: Color,
  val dark: Color,
  val darkEnd: Color,
) {
  Storage(Color(0xFF00B8D4), Color(0xFF2979FF), Color(0xFF4DD0E1), Color(0xFF5C9CFF)),
  Memory(Color(0xFFF50057), Color(0xFFFF6D3F), Color(0xFFFF5C8A), Color(0xFFFF8A65)),
  Neutral(Color(0xFF5B6BFF), Color(0xFF8B5CF6), Color(0xFF9AA6FF), Color(0xFFB794F6));

  @Composable
  fun base(): Color = if (isSystemInDarkTheme()) dark else light

  @Composable
  fun gradient(): Brush {
    val dark = isSystemInDarkTheme()
    return Brush.linearGradient(
      listOf(if (dark) this.dark else light, if (dark) darkEnd else lightEnd),
    )
  }
}
