package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun usageStatusColor(fraction: Float): Color {
  val colors = LocalStatusColors.current
  val f = fraction.coerceIn(0f, 1f)
  return when {
    f < 0.6f -> colors.success
    f < 0.85f -> colors.warning
    else -> colors.error
  }
}
