package com.ryccoatika.sqatoolkit.devinfo.ui.info.device

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item

@Immutable
internal data class DeviceViewState(
  val items: List<Item>,
  val isLoading: Boolean,
) {
  companion object {
    val Empty = DeviceViewState(
      items = emptyList(),
      isLoading = true,
    )
  }
}
