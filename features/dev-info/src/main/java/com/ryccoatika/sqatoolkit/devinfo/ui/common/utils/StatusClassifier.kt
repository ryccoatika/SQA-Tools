package com.ryccoatika.sqatoolkit.devinfo.ui.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ryccoatika.sqatoolkit.common.ui.theme.LocalStatusColors

internal enum class StatusKind { SUCCESS, WARNING, ERROR, NEUTRAL }

internal fun statusKindOf(value: String): StatusKind = when (value.trim().lowercase()) {
  "supported", "yes", "enforcing", "detected", "ready", "true", "enabled" -> StatusKind.SUCCESS
  "permissive", "unknown", "roaming", "not charging" -> StatusKind.WARNING
  "not supported", "no", "not detected", "absent", "not installed", "false", "disabled", "not available" -> StatusKind.ERROR
  else -> StatusKind.NEUTRAL
}

@Composable
internal fun statusColorFor(value: String): Color? {
  val colors = LocalStatusColors.current
  return when (statusKindOf(value)) {
    StatusKind.SUCCESS -> colors.success
    StatusKind.WARNING -> colors.warning
    StatusKind.ERROR -> colors.error
    StatusKind.NEUTRAL -> null
  }
}
