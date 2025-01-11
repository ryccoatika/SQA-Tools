package com.ryccoatika.sqatools.fillstorage.core.utils

import android.content.Context
import com.ryccoatika.sqatools.fillstorage.R
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatools.fillstorage.core.model.Storage

internal class FillStorageTextCreator(
  private val context: Context,
) {
  fun errorMessage(t: Throwable): String {
    return when {
      else -> t.localizedMessage ?: ""
    }
  }

  fun storageTypeCardTitle(
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
    withDecimal: Boolean = true,
  ): String {
    val format = if (withDecimal) "%.2f" else "%.0f"
    val capacityText = format.format(capacity)
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

  fun storageUsedSpaceText(
    storage: Storage,
  ): String {
    return storageCapacityText(
      capacity = storage.usedSpace,
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

  fun storageTypeManageTitle(
    type: Storage.Type,
  ): String {
    return when (type) {
      is Storage.Type.External -> context.getString(R.string.fs_title_manage_external)
      Storage.Type.Internal -> context.getString(R.string.fs_title_manage_internal)
      Storage.Type.Unknown -> context.getString(R.string.fs_title_manage_unknown)
    }
  }

  fun percentageText(value: Float): String {
    return percentageText((value * 100).toInt())
  }

  private fun percentageText(value: Int): String {
    return "$value%"
  }

  fun fillStorageButtonText(
    fillStorage: FillStorage,
  ): String {
    return when (fillStorage.type) {
      FillStorage.Type.MB -> storageCapacityText(fillStorage.value, Storage.Metric.MB, false)
      FillStorage.Type.GB -> storageCapacityText(fillStorage.value, Storage.Metric.GB, false)
      FillStorage.Type.PERCENT -> percentageText(fillStorage.value.toInt())
    }
  }

  fun fillStorageProgress(
    progress: FillStorage.Progress,
  ): String {
    val filledSize = storageCapacityText(
      capacity = progress.mbFilled,
      metric = Storage.Metric.MB,
      withDecimal = false,
    )
    val toFillSize = storageCapacityText(
      capacity = progress.mbFill,
      metric = Storage.Metric.MB,
      withDecimal = false,
    )
    return context.getString(R.string.fs_desc_fill_progress, filledSize, toFillSize)
  }

  fun fillStorageProgressButtonText(
    progress: FillStorage.Progress,
  ): String {
    return when {
      progress.isSuccess ||
        progress.error != null -> context.getString(R.string.fs_button_close)
      else -> context.getString(R.string.fs_button_cancel)
    }
  }
}
