package com.ryccoatika.sqatoolkit.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

interface SQAFeature {
  val featureId: String
  val icon: ImageVector

  @get:StringRes
  val featureTitle: Int

  @get:StringRes
  val featureDescription: Int

  fun open(context: Context)
}
