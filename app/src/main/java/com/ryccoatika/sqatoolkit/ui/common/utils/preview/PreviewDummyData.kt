@file:Suppress("ktlint:standard:filename")

package com.ryccoatika.sqatoolkit.ui.common.utils.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SdStorage
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.feature.FeatureDescriptor
import com.ryccoatika.sqatoolkit.feature.FeatureInstallState

internal val previewFeatureDescriptor = FeatureDescriptor(
  featureId = "PREVIEW_FEATURE",
  moduleName = "previewfeature",
  title = R.string.feature_title_fill_storage,
  description = R.string.feature_desc_fill_storage,
  icon = Icons.Rounded.SdStorage,
  activityFqn = "com.ryccoatika.sqatoolkit.preview.PreviewActivity",
  accent = FeatureAccent.Storage,
)

internal class FeatureCardPreviewParameterProvider : PreviewParameterProvider<FeatureInstallState> {
  override val values: Sequence<FeatureInstallState>
    get() = sequenceOf(
      FeatureInstallState.NotInstalled,
      FeatureInstallState.Downloading(0.42f),
      FeatureInstallState.Installing,
      FeatureInstallState.Installed,
      FeatureInstallState.Failed("Install failed (-6)"),
    )
}
