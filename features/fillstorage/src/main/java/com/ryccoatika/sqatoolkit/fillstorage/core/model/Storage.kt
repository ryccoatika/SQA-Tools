package com.ryccoatika.sqatoolkit.fillstorage.core.model

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Store byte by default
 * use[capacityInGB] to convert to other units
 * */
internal data class Storage(
  val type: Type,
  val path: String,
  val capacity: Capacity,
) {
  sealed interface Type {
    data object Internal : Type
    data class External(
      val name: String,
    ) : Type

    data object Unknown : Type
  }

  data class Capacity(
    val totalSpace: BigDecimal,
    val freeSpace: BigDecimal,
    val usedSpace: BigDecimal,
    val dummyFiles: BigDecimal,
  ) {
    val nonDummyFiles: BigDecimal
      get() = usedSpace - dummyFiles
  }

  val capacityInGB: Capacity
    get() = capacity.copy(
      totalSpace = capacity.totalSpace / 1024 / 1024 / 1024,
      freeSpace = capacity.freeSpace / 1024 / 1024 / 1024,
      usedSpace = capacity.usedSpace / 1024 / 1024 / 1024,
      dummyFiles = capacity.dummyFiles / 1024 / 1024 / 1024,
    )

  val usedSpacePercent: Float
    get() = with(capacity) {
      try {
        usedSpace.divide(totalSpace, 2, RoundingMode.HALF_UP)
      } catch (_: ArithmeticException) {
        0
      }
    }.toFloat()

  val nonDummyFilesPercent: Float
    get() = with(capacity) {
      try {
        nonDummyFiles.divide(totalSpace, 2, RoundingMode.HALF_UP)
      } catch (_: ArithmeticException) {
        0
      }
    }.toFloat()

  val dummyFilesPercent: Float
    get() = with(capacity) {
      try {
        dummyFiles.divide(totalSpace, 2, RoundingMode.HALF_UP)
      } catch (_: ArithmeticException) {
        0
      }
    }.toFloat()

  val freeSpacePercent: Float
    get() = with(capacity) {
      try {
        freeSpace.divide(totalSpace, 2, RoundingMode.HALF_UP)
      } catch (_: ArithmeticException) {
        0
      }
    }.toFloat()

  operator fun BigDecimal.div(other: Int): BigDecimal {
    return this.divide(other.toBigDecimal(), 2, RoundingMode.HALF_UP)
  }

  companion object {
    val Empty = Storage(
      type = Type.Unknown,
      path = "",
      capacity = Capacity(
        totalSpace = BigDecimal.ZERO,
        freeSpace = BigDecimal.ZERO,
        usedSpace = BigDecimal.ZERO,
        dummyFiles = BigDecimal.ZERO,
      ),
    )
  }
}
