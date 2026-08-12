package com.ryccoatika.sqatoolkit.fillstorage.ui.manage

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatoolkit.fillstorage.core.model.Storage

@Immutable
internal data class ManageViewState(
  val storage: Storage,
  val fillStorageProgress: FillStorage.Progress?,
) {
  companion object {
    val Empty = ManageViewState(
      storage = Storage.Empty,
      fillStorageProgress = null,
    )
  }
}
