package com.ryccoatika.sqatools.fillstorage.core.model

internal data class Storage(
  val type: Type,
  val path: String,
  val totalSpace: Float,
  val freeSpace: Float,
  val usedSpace: Float,
  val metric: Metric,
) {
  sealed interface Type {
    data object Internal : Type
    data class External(
      val name: String,
    ) : Type

    data object Unknown : Type
  }

  enum class Metric {
    MB,
    GB,
  }

  fun convert(to: Metric): Storage {
    return when (metric) {
      Metric.MB -> {
        when (to) {
          Metric.MB -> this
          Metric.GB -> this.copy(
            totalSpace = this.totalSpace / 1024,
            freeSpace = this.freeSpace / 1024,
            usedSpace = this.usedSpace / 1024,
            metric = Metric.GB,
          )
        }
      }

      Metric.GB -> {
        when (to) {
          Metric.MB -> this.copy(
            totalSpace = this.totalSpace * 1024,
            freeSpace = this.freeSpace * 1024,
            usedSpace = this.usedSpace * 1024,
            metric = Metric.MB,
          )

          Metric.GB -> this
        }
      }
    }
  }

  companion object {
    val Empty = Storage(
      type = Type.Unknown,
      path = "",
      totalSpace = 0f,
      freeSpace = 0f,
      usedSpace = 0f,
      metric = Metric.GB,
    )
  }
}
