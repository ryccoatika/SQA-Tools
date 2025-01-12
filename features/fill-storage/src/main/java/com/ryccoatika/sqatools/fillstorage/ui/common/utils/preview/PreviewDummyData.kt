package com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ryccoatika.sqatools.fillstorage.core.model.DummyFile
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.ui.dummyfiles.DummyFilesViewState
import com.ryccoatika.sqatools.fillstorage.ui.home.HomeViewState
import java.math.BigDecimal

private val storageUnknown = Storage(
  type = Storage.Type.Unknown,
  path = "/storage/0",
  capacity = Storage.Capacity(
    totalSpace = BigDecimal(100),
    freeSpace = BigDecimal(50),
    usedSpace = BigDecimal(50),
    dummyFiles = BigDecimal(0),
  ),
)

private val storageInternal = Storage(
  type = Storage.Type.Internal,
  path = "/storage/1",
  capacity = Storage.Capacity(
    totalSpace = BigDecimal(200),
    freeSpace = BigDecimal(150),
    usedSpace = BigDecimal(50),
    dummyFiles = BigDecimal(0),
  ),
)

private val storageExternal = Storage(
  type = Storage.Type.External("3423-4859"),
  path = "/storage/2",
  capacity = Storage.Capacity(
    totalSpace = BigDecimal(120),
    freeSpace = BigDecimal(20),
    usedSpace = BigDecimal(100),
    dummyFiles = BigDecimal(0),
  ),
)

private val storageAlmostFull = Storage(
  type = Storage.Type.Internal,
  path = "/storage/2",
  capacity = Storage.Capacity(
    totalSpace = BigDecimal(120),
    freeSpace = BigDecimal(10),
    usedSpace = BigDecimal(110),
    dummyFiles = BigDecimal(0),
  ),
)

private val storageFull = Storage(
  type = Storage.Type.Internal,
  path = "/storage/2",
  capacity = Storage.Capacity(
    totalSpace = BigDecimal(120),
    freeSpace = BigDecimal(0),
    usedSpace = BigDecimal(12),
    dummyFiles = BigDecimal(0),
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
