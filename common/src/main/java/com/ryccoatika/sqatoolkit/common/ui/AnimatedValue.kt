package com.ryccoatika.sqatoolkit.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun animatedFraction(target: Float): State<Float> =
  animateFloatAsState(targetValue = target.coerceIn(0f, 1f), label = "fraction")

@Composable
fun AnimatedCountText(
  value: Float,
  formatter: (Float) -> String,
  modifier: Modifier = Modifier,
  style: TextStyle = LocalTextStyle.current,
  color: Color = Color.Unspecified,
) {
  val animated by animateFloatAsState(targetValue = value, label = "count")
  Text(text = formatter(animated), modifier = modifier, style = style, color = color)
}
