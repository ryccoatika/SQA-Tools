package com.ryccoatika.sqatoolkit.common.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent

@Composable
fun GradientHero(
  title: String,
  accent: FeatureAccent,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  trailing: @Composable (() -> Unit)? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
      .background(accent.gradient())
      .padding(horizontal = 20.dp, vertical = 24.dp),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.weight(1f),
    ) {
      Text(title, style = MaterialTheme.typography.titleLarge, color = Color.White)
      if (subtitle != null) {
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
      }
    }
    if (trailing != null) trailing()
  }
}
