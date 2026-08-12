package com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ryccoatika.sqatoolkit.fillstorage.core.model.DummyFile
import com.ryccoatika.sqatoolkit.fillstorage.core.model.Storage
import com.ryccoatika.sqatoolkit.fillstorage.ui.dummyfiles.DummyFilesViewState
import com.ryccoatika.sqatoolkit.fillstorage.ui.home.HomeViewState

private val storageUnknown = Storage(
  type = Storage.Type.Unknown,
  path = "/storage/0",
  capacity = Storage.Capacity(
    totalSpace = 100.toBigDecimal(),
    freeSpace = 50.toBigDecimal(),
    usedSpace = 50.toBigDecimal(),
    dummyFiles = 0.toBigDecimal(),
  ),
)

private val storageInternal = Storage(
  type = Storage.Type.Internal,
  path = "/storage/1",
  capacity = Storage.Capacity(
    totalSpace = 200.toBigDecimal(),
    freeSpace = 150.toBigDecimal(),
    usedSpace = 50.toBigDecimal(),
    dummyFiles = 0.toBigDecimal(),
  ),
)

private val storageExternal = Storage(
  type = Storage.Type.External("3423-4859"),
  path = "/storage/2",
  capacity = Storage.Capacity(
    totalSpace = 120.toBigDecimal(),
    freeSpace = 20.toBigDecimal(),
    usedSpace = 100.toBigDecimal(),
    dummyFiles = 0.toBigDecimal(),
  ),
)

private val storageAlmostFull = Storage(
  type = Storage.Type.Internal,
  path = "/storage/2",
  capacity = Storage.Capacity(
    totalSpace = 120.toBigDecimal(),
    freeSpace = 10.toBigDecimal(),
    usedSpace = 110.toBigDecimal(),
    dummyFiles = 0.toBigDecimal(),
  ),
)

private val storageFull = Storage(
  type = Storage.Type.Internal,
  path = "/storage/2",
  capacity = Storage.Capacity(
    totalSpace = 120.toBigDecimal(),
    freeSpace = 0.toBigDecimal(),
    usedSpace = 12.toBigDecimal(),
    dummyFiles = 0.toBigDecimal(),
  ),
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

internal class DummyFilesPreviewParameterProvider : PreviewParameterProvider<DummyFilesViewState> {
  override val values: Sequence<DummyFilesViewState>
    get() = sequenceOf(
      DummyFilesViewState(
        files = emptyList(),
        isLoading = false,
      ),
      DummyFilesViewState(
        files = listOf(
          DummyFile(
            name = "Hello.txt",
            path = "/Hello.txt",
            sizeInMB = 1024.0,
          ),
        ),
        isLoading = false,
      ),
      DummyFilesViewState(
        files = listOf(
          DummyFile(
            name = "Hello.txt",
            path = "/Hello.txt",
            sizeInMB = 2048.0,
          ),
          DummyFile(
            name = "World.txt",
            path = "/World.txt",
            sizeInMB = 4096.0,
          ),
        ),
        isLoading = true,
      ),
    )
}
