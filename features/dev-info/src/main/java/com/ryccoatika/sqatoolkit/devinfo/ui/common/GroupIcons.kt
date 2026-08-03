package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.DeveloperBoard
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Monitor
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem

internal fun groupIcon(title: String): ImageVector {
  val t = title.lowercase()
  return when {
    t.contains("processor") || t.contains("cpu") -> Icons.Rounded.DeveloperBoard
    t.contains("gpu") || t.contains("graphic") -> Icons.Rounded.Monitor
    t.contains("memory") || t.contains("ram") -> Icons.Rounded.Memory
    t.contains("storage") -> Icons.Rounded.Storage
    t.contains("display") -> Icons.Rounded.Monitor
    t.contains("battery") -> Icons.Rounded.BatteryFull
    t.contains("android") -> Icons.Rounded.Android
    t.contains("system") -> Icons.Rounded.Security
    t.contains("sim") || t.contains("network") || t.contains("telephony") -> Icons.Rounded.SignalCellularAlt
    t.contains("wi-fi") || t.contains("wifi") -> Icons.Rounded.Wifi
    t.contains("bluetooth") -> Icons.Rounded.Bluetooth
    else -> Icons.Rounded.Info
  }
}

/**
 * Builds a copyable "label: value" block from a group's rows.
 * Only free-text rows (RawTextItem/PermissionItem) are included; Label-based rows are skipped.
 */
internal fun groupItemsToText(items: List<Item>): String = buildString {
  items.forEach { item ->
    when (item) {
      is RawTextItem -> appendLine("${item.label}: ${item.value}")
      is PermissionItem -> appendLine("${item.label}: ${item.value}")
      else -> Unit
    }
  }
}.trim()
