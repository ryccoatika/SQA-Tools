package com.ryccoatika.sqatoolkit.fillstorage.ui.dummyfiles

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.fillstorage.core.model.DummyFile

@Immutable
internal data class DummyFilesViewState(
  val files: List<DummyFile>,
  val isLoading: Boolean,
) {
  companion object {
    val Empty = DummyFilesViewState(
      files = emptyList(),
      isLoading = false,
    )
  }
}
