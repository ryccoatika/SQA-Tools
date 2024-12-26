package com.ryccoatika.sqatools.fillstorage.core.model

internal data class Storage(
  val type: Type,
  val path: String,
  val totalSpace: Float,
  val freeSpace: Float,
  val usedSpace: Float,
  val metrics: Metrics,
) {
  sealed interface Type {
    data object Internal : Type
    data class External(
      val name: String,
    ) : Type

    data object Unknown : Type
  }

  enum class Metrics {
    KB,
    MB,
    GB,
  }

  fun convert(to: Metrics): Storage {
    return when (metrics) {
      Metrics.KB -> {
        when (to) {
          Metrics.KB -> this
          Metrics.MB -> this.copy(
            totalSpace = this.totalSpace / 1024,
            freeSpace = this.freeSpace / 1024,
            usedSpace = this.usedSpace / 1024,
            metrics = Metrics.MB,
          )

          Metrics.GB -> this.copy(
            totalSpace = this.totalSpace / 1024 / 1024,
            freeSpace = this.freeSpace / 1024 / 1024,
            usedSpace = this.usedSpace / 1024 / 1024,
            metrics = Metrics.GB,
          )
        }
      }

      Metrics.MB -> {
        when (to) {
          Metrics.KB -> this.copy(
            totalSpace = this.totalSpace * 1024,
            freeSpace = this.freeSpace * 1024,
            usedSpace = this.usedSpace * 1024,
            metrics = Metrics.MB,
          )

          Metrics.MB -> this
          Metrics.GB -> this.copy(
            totalSpace = this.totalSpace / 1024,
            freeSpace = this.freeSpace / 1024,
            usedSpace = this.usedSpace / 1024,
            metrics = Metrics.GB,
          )
        }
      }

      Metrics.GB -> {
        when (to) {
          Metrics.KB -> this.copy(
            totalSpace = this.totalSpace * 1024 * 1024,
            freeSpace = this.freeSpace * 1024 * 1024,
            usedSpace = this.usedSpace * 1024 * 1024,
            metrics = Metrics.GB,
          )

          Metrics.MB -> this.copy(
            totalSpace = this.totalSpace * 1024,
            freeSpace = this.freeSpace * 1024,
            usedSpace = this.usedSpace * 1024,
            metrics = Metrics.MB,
          )

          Metrics.GB -> this
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
      metrics = Metrics.MB,
    )
  }
}
