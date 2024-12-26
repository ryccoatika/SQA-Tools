package com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.ui.home.HomeViewState

private val storageUnknown = Storage(
  type = Storage.Type.Unknown,
  path = "/storage/0",
  totalSpace = 100f,
  freeSpace = 50f,
  usedSpace = 50f,
  metrics = Storage.Metrics.KB,
)

private val storageInternal = Storage(
  type = Storage.Type.Internal,
  path = "/storage/1",
  totalSpace = 200f,
  freeSpace = 150f,
  usedSpace = 50f,
  metrics = Storage.Metrics.MB,
)

private val storageExternal = Storage(
  type = Storage.Type.External("3423-4859"),
  path = "/storage/2",
  totalSpace = 120f,
  freeSpace = 20f,
  usedSpace = 100f,
  metrics = Storage.Metrics.GB,
)

private val storageAlmostFull = Storage(
  type = Storage.Type.Internal,
  path = "/storage/2",
  totalSpace = 120f,
  freeSpace = 10f,
  usedSpace = 110f,
  metrics = Storage.Metrics.GB,
)

private val storageFull = Storage(
  type = Storage.Type.Internal,
  path = "/storage/2",
  totalSpace = 120f,
  freeSpace = 0f,
  usedSpace = 120f,
  metrics = Storage.Metrics.GB,
)

internal class StoragePreviewParameterProvider : PreviewParameterProvider<Storage> {
  override val values: Sequence<Storage>
    get() = sequenceOf(
      storageUnknown,
      storageInternal,
      storageExternal,
      storageAlmostFull,
      storageFull,
    )
}

internal class HomePreviewParameterProvider : PreviewParameterProvider<HomeViewState> {
  override val values: Sequence<HomeViewState>
    get() = sequenceOf(
      HomeViewState(
        storages = emptyList(),
      ),
      HomeViewState(
        storages = listOf(
          storageInternal,
        ),
      ),
      HomeViewState(
        storages = listOf(
          storageInternal,
          storageExternal,
        ),
      ),
    )
}
