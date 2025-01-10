package com.ryccoatika.sqatools.fillstorage.ui.manage

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatools.fillstorage.core.model.Storage

@Immutable
internal data class ManageViewState(
  val storage: Storage,
) {
  companion object {
    val Empty = ManageViewState(
      storage = Storage.Empty,
    )
  }
}
