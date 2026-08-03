package com.ryccoatika.sqatoolkit.devinfo.ui.info.common

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item

@Immutable
internal data class InfoViewState(
  val items: List<Item>,
  val isLoading: Boolean,
) {
  companion object {
    val Empty = InfoViewState(
      items = emptyList(),
      isLoading = true,
    )
  }
}
