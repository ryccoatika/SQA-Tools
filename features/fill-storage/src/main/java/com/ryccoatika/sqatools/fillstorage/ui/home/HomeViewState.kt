package com.ryccoatika.sqatools.fillstorage.ui.home

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatools.fillstorage.core.model.Storage

@Immutable
internal data class HomeViewState(
  val metric: Storage.Metric,
  val storages: List<Storage>,
) {
  companion object {
    val Empty = HomeViewState(
      metric = Storage.Metric.GB,
      storages = emptyList(),
    )
  }
}
