@file:Suppress("ktlint:standard:filename")

package com.ryccoatika.sqatoolkit.ui.common.utils.preview

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ryccoatika.sqatoolkit.common.SQAFeature

private val featureOne = object : SQAFeature {
  override val featureId: String
    get() = "FEATURE_ONE"
  override val icon: ImageVector
    get() = Icons.Rounded.SdStorage
  override val featureTitle: Int
    get() = android.R.string.untitled
  override val featureDescription: Int
    get() = android.R.string.unknownName

  override fun open(context: Context) {
  }
}
private val featureTwo = object : SQAFeature {
  override val featureId: String
    get() = "FEATURE_TWO"
  override val icon: ImageVector
    get() = Icons.Rounded.Storage
  override val featureTitle: Int
    get() = android.R.string.untitled
  override val featureDescription: Int
    get() = android.R.string.unknownName

  override fun open(context: Context) {
  }
}

internal class FeatureCardPreviewParameterProvider : PreviewParameterProvider<SQAFeature> {
  override val values: Sequence<SQAFeature>
    get() = sequenceOf(
      featureOne,
      featureTwo,
    )
}

internal class ToolsPreviewParameterProvider : PreviewParameterProvider<Set<SQAFeature>> {
  override val values: Sequence<Set<SQAFeature>>
    get() = sequenceOf(
      setOf(featureOne),
      setOf(featureOne, featureTwo),
      emptySet(),
    )
}
