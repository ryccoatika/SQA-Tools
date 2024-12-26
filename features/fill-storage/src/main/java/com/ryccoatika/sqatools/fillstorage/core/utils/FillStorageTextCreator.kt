package com.ryccoatika.sqatools.fillstorage.core.utils

import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class FillStorageTextCreator {
  fun storageTypeTitle(
    type: Storage.Type,
  ): String {
    return when (type) {
      is Storage.Type.External -> {
        "External (${type.name})"
      }
      Storage.Type.Internal -> {
        "Internal"
      }
      Storage.Type.Unknown -> {
        "Unknown"
      }
    }
  }

  private fun storageCapacityText(
    capacity: Float,
    metrics: Storage.Metrics,
  ): String {
    return when (metrics) {
      Storage.Metrics.KB -> "$capacity KB"
      Storage.Metrics.MB -> "$capacity MB"
      Storage.Metrics.GB -> "$capacity GB"
    }
  }

  fun storageFreeSpaceText(
    storage: Storage,
  ): String {
    return storageCapacityText(
      capacity = storage.freeSpace,
      metrics = storage.metrics,
    )
  }

  fun storageCapacityDetailText(
    storage: Storage,
  ): String {
    val usedCapacity = storageCapacityText(
      capacity = storage.usedSpace,
      metrics = storage.metrics,
    )
    val totalCapacity = storageCapacityText(
      capacity = storage.totalSpace,
      metrics = storage.metrics,
    )

    return "$usedCapacity of $totalCapacity"
  }
}
