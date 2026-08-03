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
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.itemPlainText

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
 * Renders every row type (raw-text, permission, and Label-based rows) via the same
 * [itemPlainText] mapping used by search and the share report, so every group card
 * gets a working copy button.
 */
internal fun groupItemsToText(
  items: List<Item>,
  textCreator: DevInfoTextCreator,
  supportedText: String,
  notSupportedText: String,
): String = buildString {
  items.forEach { item ->
    itemPlainText(item, textCreator, supportedText, notSupportedText)?.let { appendLine(it) }
  }
}.trim()
