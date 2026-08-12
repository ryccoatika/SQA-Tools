package com.ryccoatika.sqatoolkit.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.SdStorage
import com.ryccoatika.sqatoolkit.R

internal object FeatureRegistry {
  val features: List<FeatureDescriptor> = listOf(
    FeatureDescriptor(
      featureId = "FILL_STORAGE",
      moduleName = "fillstorage",
      title = R.string.feature_title_fill_storage,
      description = R.string.feature_desc_fill_storage,
      icon = Icons.Rounded.SdStorage,
      activityFqn = "com.ryccoatika.sqatoolkit.fillstorage.FillStorageActivity",
    ),
    FeatureDescriptor(
      featureId = "FILL_MEMORY",
      moduleName = "fillmemory",
      title = R.string.feature_title_fill_memory,
      description = R.string.feature_desc_fill_memory,
      icon = Icons.Rounded.Memory,
      activityFqn = "com.ryccoatika.sqatoolkit.fillmemory.FillMemoryActivity",
    ),
  )
}
