package com.ryccoatika.sqatools.fillstorage.core.utils

import android.content.Context
import android.os.StatFs
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.inject.FillStorageScope
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class StorageHelper(
  private val context: Context,
) {
  private fun getStorageTypeByPath(path: String): Storage.Type {
    val prefix = "/storage/"
    val suffix = "/Android/data/${context.packageName}/files"

    val sanitizedPath = path
      .removePrefix(prefix)
      .removeSuffix(suffix)

    return when {
      sanitizedPath.contains("emulated") -> Storage.Type.Internal
      else -> Storage.Type.External(sanitizedPath)
    }
  }

  fun getStorageCapacity(path: String): Storage {
    val stat = StatFs(path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong

    val totalSpace = blockSize * totalBlocks
    val freeSpace = blockSize * availableBlocks
    val usedSpace = totalSpace - freeSpace

    return Storage(
      type = getStorageTypeByPath(path),
      path = path,
      totalSpace = totalSpace / 1024f / 1024f,
      freeSpace = freeSpace / 1024f / 1024f,
      usedSpace = usedSpace / 1024f / 1024f,
      metric = Storage.Metric.MB,
    ).convert(Storage.Metric.GB)
  }

  fun getAllStorageCapacity(): List<Storage> {
    return context.getExternalFilesDirs("").map { file ->
      val storageCapacity = getStorageCapacity(file.path)
      storageCapacity
    }
  }
}
