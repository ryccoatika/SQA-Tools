package com.ryccoatika.sqatools.fillmemory.core.utils

import android.content.Context
import com.ryccoatika.sqatools.fillmemory.R
import com.ryccoatika.sqatools.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatools.fillmemory.core.model.MemoryUsage

internal class FillMemoryTextCreator(
  private val context: Context,
) {
  fun errorMessage(t: Throwable): String {
    return when {
      else -> t.localizedMessage.orEmpty()
    }
  }

  fun percentText(value: Float): String {
    return percentText((value * 100).toInt())
  }

  fun percentText(value: Int): String {
    return context.getString(R.string.fm_text_percent, value)
  }

  fun memoryTotalText(memoryUsage: MemoryUsage): String {
    return context.getString(R.string.fm_text_memory_total, memoryUsage.totalMemoryInMB)
  }

  fun memoryFreeText(memoryUsage: MemoryUsage): String {
    return context.getString(R.string.fm_text_memory_free, memoryUsage.availableMemoryInMB)
  }

  fun memoryUsedText(memoryUsage: MemoryUsage): String {
    return context.getString(R.string.fm_text_memory_used, memoryUsage.usedMemoryInMB)
  }

  fun memoryDummyText(memoryUsage: MemoryUsage): String {
    return context.getString(R.string.fm_text_memory_dummy, memoryUsage.dummyMemoryInMB)
  }

  fun fillMemoryButtonText(
    fillMemory: FillMemory,
  ): String {
    return context.getString(
      R.string.fm_text_mb_value_no_decimal,
      fillMemory.value,
    )
  }

  fun fillMemoryProgress(
    progress: FillMemory.Progress,
  ): String {
    return context.getString(R.string.fm_desc_fill_progress, progress.mbFilled, progress.mbFill)
  }

  fun fillMemoryProgressButtonText(
    progress: FillMemory.Progress,
  ): String {
    return when {
      progress.isSuccess ||
        progress.error != null -> context.getString(R.string.fm_button_close)

      else -> context.getString(R.string.fm_button_cancel)
    }
  }
}
