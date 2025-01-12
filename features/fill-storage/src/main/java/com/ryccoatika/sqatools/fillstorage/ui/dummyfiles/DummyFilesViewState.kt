package com.ryccoatika.sqatools.fillstorage.ui.dummyfiles

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatools.fillstorage.core.model.DummyFile

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
