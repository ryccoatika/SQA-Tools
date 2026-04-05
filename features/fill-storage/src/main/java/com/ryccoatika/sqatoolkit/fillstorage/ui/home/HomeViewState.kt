package com.ryccoatika.sqatoolkit.fillstorage.ui.home

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.fillstorage.core.model.Storage

@Immutable
internal data class HomeViewState(
  val storages: List<Storage>,
) {
  companion object {
    val Empty = HomeViewState(
      storages = emptyList(),
    )
  }
}
