package com.ryccoatika.sqatoolkit.devinfo.core.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun Instant.toLocalDate(): LocalDate {
  return LocalDate.ofInstant(this, ZoneId.systemDefault())
}

internal fun DateTimeFormatter.formatDate(instant: Instant): String {
  return format(instant.toLocalDate())
}
