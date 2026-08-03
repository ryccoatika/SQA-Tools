package com.ryccoatika.sqatoolkit.devinfo.ui.main

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType

@Immutable
internal data class TabData(
  val type: DevInfoType,
  val items: List<Item>,
  val isLoading: Boolean,
)

@Immutable
internal data class DevInfoViewState(
  val tabs: List<TabData>,
  val query: String,
) {
  companion object {
    val Empty = DevInfoViewState(tabs = emptyList(), query = "")
  }
}
