package com.ryccoatika.sqatoolkit.feature

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

internal data class FeatureDescriptor(
  val featureId: String,
  val moduleName: String,
  @get:StringRes val title: Int,
  @get:StringRes val description: Int,
  val icon: ImageVector,
  val activityFqn: String,
)
