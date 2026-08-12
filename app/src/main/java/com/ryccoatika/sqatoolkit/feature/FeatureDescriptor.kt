package com.ryccoatika.sqatoolkit.feature

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

internal data class FeatureDescriptor(
  val featureId: String,
  val moduleName: String,
  @param:StringRes val title: Int,
  @param:StringRes val description: Int,
  val icon: ImageVector,
  val activityFqn: String,
)
