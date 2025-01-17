package com.ryccoatika.sqatools.fillmemory.core.utils

import android.app.ActivityManager
import android.content.Context
import com.ryccoatika.sqatools.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatools.fillmemory.core.model.MemoryUsage
import com.ryccoatika.sqatools.fillmemory.inject.FillMemoryScope
import java.math.BigDecimal
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import me.tatarka.inject.annotations.Inject

@FillMemoryScope
@Inject
internal class MemoryHelper(
  private val context: Context,
) {

  init {
    System.loadLibrary("fill-memory")
  }

  private fun getMemoryUsage(): MemoryUsage {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager?.getMemoryInfo(memoryInfo)

    return MemoryUsage(
      totalMemory = BigDecimal(memoryInfo.totalMem),
      availableMemory = BigDecimal(memoryInfo.availMem),
      dummyMemory = BigDecimal(getAllocatedMemorySize()),
    )
  }

  fun observeMemoryUsage(): Flow<MemoryUsage> = flow {
    while (coroutineContext.isActive) {
      emit(getMemoryUsage())
      delay(1.seconds)
    }
  }.flowOn(Dispatchers.IO)

  suspend fun fillMemory(fillMemory: FillMemory, onProgress: (FillMemory.Progress) -> Unit) {
    val fileSizeInBytes = (fillMemory.value * 1024 * 1024).toLong()
    val bufferSize = 1024

    var bytesToWrite: Long
    var bytesWritten = 0L
    var progress = 0f

    try {
      while (coroutineContext.isActive && bytesWritten < fileSizeInBytes) {
        bytesToWrite = minOf(bufferSize.toLong(), fileSizeInBytes - bytesWritten)

        // allocate memory using c++
        allocateMemory(bytesToWrite)

        bytesWritten += bytesToWrite

        progress = bytesWritten.toFloat() / fileSizeInBytes.toFloat()

        onProgress(
          FillMemory.Progress(
            progress = progress,
            mbFilled = bytesToMB(bytesWritten),
            mbFill = bytesToMB(fileSizeInBytes),
            isSuccess = false,
            error = null,
          ),
        )
      }

      onProgress(
        FillMemory.Progress(
          progress = progress,
          mbFilled = bytesToMB(bytesWritten),
          mbFill = bytesToMB(fileSizeInBytes),
          isSuccess = true,
          error = null,
        ),
      )
    } catch (e: Throwable) {
      onProgress(
        FillMemory.Progress(
          progress = progress,
          mbFilled = bytesToMB(bytesWritten),
          mbFill = bytesToMB(fileSizeInBytes),
          isSuccess = false,
          error = e,
        ),
      )
    }
  }

  fun clearAllMemory() {
    deallocateAllMemory()
  }

  private fun bytesToMB(bytes: Long): Double {
    return bytes / 1024.0 / 1024.0
  }

  private external fun getAllocatedMemorySize(): Long

  private external fun allocateMemory(size: Long)

  private external fun deallocateAllMemory()
}
