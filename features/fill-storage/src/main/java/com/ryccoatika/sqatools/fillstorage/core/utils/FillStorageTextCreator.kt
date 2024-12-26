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

  fun storageMetricText(
    metric: Storage.Metric,
  ): String {
    return when (metric) {
      Storage.Metric.MB -> "MB"
      Storage.Metric.GB -> "GB"
    }
  }

  private fun storageCapacityText(
    capacity: Float,
    metric: Storage.Metric,
  ): String {
    val capacityText = "%.2f".format(capacity)
    return "$capacityText ${storageMetricText(metric)}"
  }

  fun storageFreeSpaceText(
    storage: Storage,
  ): String {
    return storageCapacityText(
      capacity = storage.freeSpace,
      metric = storage.metric,
    )
  }

  fun storageCapacityDetailText(
    storage: Storage,
  ): String {
    val usedCapacity = storageCapacityText(
      capacity = storage.usedSpace,
      metric = storage.metric,
    )
    val totalCapacity = storageCapacityText(
      capacity = storage.totalSpace,
      metric = storage.metric,
    )

    return "$usedCapacity of $totalCapacity"
  }
}
