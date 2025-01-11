package com.ryccoatika.sqatools.fillstorage.core.utils

import android.content.Context
import android.os.Build
import android.os.FileObserver
import android.os.StatFs
import androidx.annotation.RequiresApi
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.inject.FillStorageScope
import java.io.File
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
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
      totalSpace = bytesToMB(totalSpace),
      freeSpace = bytesToMB(freeSpace),
      usedSpace = bytesToMB(usedSpace),
      metric = Storage.Metric.MB,
    ).convert(Storage.Metric.GB)
  }

  fun observeStorageCapacity(path: String): Flow<Storage> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      observeStorageCapacityMinApi29(path)
    } else {
      observeStorageCapacityBelowApi29(path)
    }
  }

  @RequiresApi(29)
  private fun observeStorageCapacityMinApi29(folder: String): Flow<Storage> = callbackFlow {
    trySend(getStorageCapacity(folder))

    val file = File(folder, DUMMY_FILES_FOLDER)
    val fileObserver = object : FileObserver(file, ALL_EVENTS) {
      override fun onEvent(event: Int, path: String?) {
        trySend(getStorageCapacity(folder))
      }
    }
    fileObserver.startWatching()

    awaitClose {
      fileObserver.stopWatching()
    }
  }.flowOn(Dispatchers.IO)

  private fun observeStorageCapacityBelowApi29(folder: String): Flow<Storage> = flow {
    while (coroutineContext.isActive) {
      emit(getStorageCapacity(folder))
      delay(1.seconds)
    }
  }.flowOn(Dispatchers.IO)

  fun getAllStorageCapacity(): List<Storage> {
    return context.getExternalFilesDirs("").map { file ->
      val storageCapacity = getStorageCapacity(file.path)
      storageCapacity
    }
  }

  private fun generateDummyFileName(): String {
    val timeMillis = System.currentTimeMillis()
    return "$timeMillis$DUMMY_FILE_EXT"
  }

  suspend fun fillStorage(
    storage: Storage,
    fillStorage: FillStorage,
    onProgress: (FillStorage.Progress) -> Unit,
  ) {
    val fileSizeInBytes = when (fillStorage.type) {
      FillStorage.Type.MB -> fillStorage.value * 1024 * 1024
      FillStorage.Type.GB -> fillStorage.value * 1024 * 1024 * 1024
      FillStorage.Type.PERCENT -> storage.freeSpaceInBytes * (fillStorage.value / 100)
    }.toLong()
    val bufferSize = (fileSizeInBytes / 2048).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    val directory = File(storage.path, DUMMY_FILES_FOLDER)
    if (!directory.exists()) directory.mkdirs()

    val fileName = generateDummyFileName()
    val file = File(directory, fileName)

    var bytesToWrite: Long
    var bytesWritten = 0L
    var progress = 0f
    try {
      if (!file.exists()) runBlocking { file.createNewFile() }

      val buffer = ByteArray(bufferSize)
      val outputStream = file.outputStream()

      while (coroutineContext.isActive && bytesWritten < fileSizeInBytes) {
        bytesToWrite = minOf(buffer.size.toLong(), fileSizeInBytes - bytesWritten)
        runBlocking { outputStream.write(buffer, 0, bytesToWrite.toInt()) }
        bytesWritten += bytesToWrite

        progress = bytesWritten.toFloat() / fileSizeInBytes.toFloat()

        onProgress(
          FillStorage.Progress(
            progress = progress,
            mbFilled = bytesToMB(bytesWritten),
            mbFill = bytesToMB(fileSizeInBytes),
            isSuccess = false,
            error = null,
          ),
        )
      }

      runBlocking { outputStream.close() }

      onProgress(
        FillStorage.Progress(
          progress = progress,
          mbFilled = bytesToMB(bytesWritten),
          mbFill = bytesToMB(fileSizeInBytes),
          isSuccess = true,
          error = null,
        ),
      )
    } catch (e: Exception) {
      onProgress(
        FillStorage.Progress(
          progress = progress,
          mbFilled = bytesToMB(bytesWritten),
          mbFill = bytesToMB(fileSizeInBytes),
          isSuccess = false,
          error = e,
        ),
      )
    }
  }

  private fun bytesToMB(bytes: Long): Float {
    return bytes / 1024f / 1024f
  }

  companion object {
    private const val DUMMY_FILE_EXT = ".dat"
    private const val DUMMY_FILES_FOLDER = "DummyData"
  }
}
