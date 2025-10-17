package com.ryccoatika.sqatools.fillstorage.core.utils

import android.content.Context
import android.os.Build
import android.os.FileObserver
import android.os.FileObserver.CLOSE_WRITE
import android.os.FileObserver.DELETE
import android.os.FileObserver.MOVED_FROM
import android.os.FileObserver.MOVED_TO
import android.os.StatFs
import androidx.annotation.RequiresApi
import com.ryccoatika.sqatools.common.utils.orZero
import com.ryccoatika.sqatools.fillstorage.core.error.FillPercentExceeded
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.inject.FillStorageScope
import java.io.File
import java.math.BigDecimal
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
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
    val dummyFiles = File(path, DUMMY_FILES_FOLDER).listFiles()?.sumOf { it.length() }.orZero()

    return Storage(
      type = getStorageTypeByPath(path),
      path = path,
      capacity = Storage.Capacity(
        totalSpace = BigDecimal(totalSpace),
        freeSpace = BigDecimal(freeSpace),
        usedSpace = BigDecimal(usedSpace),
        dummyFiles = BigDecimal(dummyFiles),
      ),
    )
  }

  fun observeStorageCapacity(path: String): Flow<Storage> {
    val file = File(path, DUMMY_FILES_FOLDER)
    return observeStorageEvent(file).mapLatest {
      getStorageCapacity(path)
    }
  }

  fun getDummyFilesPath(path: String): List<File> {
    val file = File(path, DUMMY_FILES_FOLDER)
    return file.listFiles()?.toList().orEmpty()
  }

  private fun observeStorageEvent(file: File): Flow<Int> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      observeFileMinApi29(file)
    } else {
      flow {
        while (coroutineContext.isActive) {
          emit(FILE_OBSERVER_MASK)
          delay(3.seconds)
        }
      }
    }
  }

  @RequiresApi(29)
  private fun observeFileMinApi29(file: File): Flow<Int> = callbackFlow {
    trySend(FILE_OBSERVER_MASK)

    val fileObserver = object : FileObserver(file, FILE_OBSERVER_MASK) {
      override fun onEvent(event: Int, path: String?) {
        trySend(event)
      }
    }
    fileObserver.startWatching()

    awaitClose {
      fileObserver.stopWatching()
    }
  }.flowOn(Dispatchers.IO)

  fun getAllStorageCapacity(): List<Storage> {
    return context.getExternalFilesDirs("").map { file ->
      getStorageCapacity(file.path)
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
      FillStorage.Type.PERCENT -> {
        val totalSpace = storage.capacity.totalSpace.toLong()
        val usedSpace = storage.capacity.usedSpace.toLong()
        val value = totalSpace * (fillStorage.value / 100)

        if (value <= usedSpace) {
          onProgress(
            FillStorage.Progress(
              progress = 0f,
              mbFilled = 0.0,
              mbFill = 0.0,
              isSuccess = false,
              error = FillPercentExceeded(fillStorage.value.toFloat()),
            ),
          )
          return
        }
        value - usedSpace
      }
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

  fun deleteAllDummyFiles(path: String) {
    File(path, DUMMY_FILES_FOLDER).deleteRecursively()
  }

  fun deleteFile(path: String) {
    File(path).delete()
  }

  fun bytesToMB(bytes: Long): Double {
    return bytes / 1024.0 / 1024.0
  }

  companion object {
    private const val DUMMY_FILE_EXT = ".dat"
    private const val DUMMY_FILES_FOLDER = "DummyData"
    private const val FILE_OBSERVER_MASK = CLOSE_WRITE or MOVED_FROM or MOVED_TO or DELETE
  }
}
