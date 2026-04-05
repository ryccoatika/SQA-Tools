package com.ryccoatika.sqatoolkit.fillstorage.core.utils

import android.content.Context
import com.ryccoatika.sqatoolkit.fillstorage.R
import com.ryccoatika.sqatoolkit.fillstorage.core.error.FillPercentExceeded
import com.ryccoatika.sqatoolkit.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatoolkit.fillstorage.core.model.Storage

internal class FillStorageTextCreator(
  private val context: Context,
) {
  fun errorMessage(t: Throwable): String {
    return when {
      t is FillPercentExceeded -> context.getString(R.string.fs_error_fill_percent, t.fillPercent)
      else -> t.localizedMessage.orEmpty()
    }
  }

  fun storageTitle(
    storage: Storage,
  ): String {
    return when (storage.type) {
      is Storage.Type.External -> {
        context.getString(R.string.fs_title_manage_external, storage.type.name)
      }

      Storage.Type.Internal -> {
        context.getString(R.string.fs_title_manage_internal)
      }

      Storage.Type.Unknown -> {
        context.getString(R.string.fs_title_manage_unknown)
      }
    }
  }

  fun storageCapacityDesc(
    storage: Storage,
  ): String {
    val usedCapacity = context.getString(
      R.string.fs_text_gb_value,
      storage.capacityInGB.usedSpace,
    )
    val totalCapacity = context.getString(
      R.string.fs_text_gb_value,
      storage.capacityInGB.totalSpace,
    )

    return context.getString(R.string.fs_desc_storage_capacity, usedCapacity, totalCapacity)
  }

  fun storageFreeSpace(
    storage: Storage,
  ): String {
    return context.getString(R.string.fs_text_gb_value, storage.capacityInGB.freeSpace)
  }

  fun storageUsedSpacePercentage(
    storage: Storage,
  ): String {
    return percentageText(storage.usedSpacePercent)
  }

  fun percentageText(value: Float): String {
    return percentageText((value * 100).toInt())
  }

  private fun percentageText(value: Int): String {
    return context.getString(R.string.fs_text_percent_value, value)
  }

  fun storageNonDummyFilesLabel(storage: Storage): String {
    return context.getString(R.string.fs_text_used_space, storage.capacityInGB.nonDummyFiles)
  }

  fun storageFreeSpaceLabel(storage: Storage): String {
    return context.getString(R.string.fs_text_free_space, storage.capacityInGB.freeSpace)
  }

  fun storageDummyFilesLabel(storage: Storage): String {
    return context.getString(R.string.fs_text_dummy_files, storage.capacityInGB.dummyFiles)
  }

  fun fillStorageButtonText(
    fillStorage: FillStorage,
  ): String {
    return when (fillStorage.type) {
      FillStorage.Type.MB -> context.getString(
        R.string.fs_text_mb_value_no_decimal,
        fillStorage.value,
      )

      FillStorage.Type.GB -> context.getString(
        R.string.fs_text_gb_value_no_decimal,
        fillStorage.value,
      )

      FillStorage.Type.PERCENT -> percentageText(fillStorage.value.toInt())
    }
  }

  fun fillStorageProgress(
    progress: FillStorage.Progress,
  ): String {
    return context.getString(R.string.fs_desc_fill_progress, progress.mbFilled, progress.mbFill)
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
