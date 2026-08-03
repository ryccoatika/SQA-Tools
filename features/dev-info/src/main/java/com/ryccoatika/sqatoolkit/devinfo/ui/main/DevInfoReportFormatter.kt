package com.ryccoatika.sqatoolkit.devinfo.ui.main

import android.content.Context
import com.ryccoatika.sqatoolkit.devinfo.core.model.DateItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ElapsedTimeItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.StatusItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import me.tatarka.inject.annotations.Inject

// Lives in ui.main (same package as TabData) — do not move to core/.
@Inject
internal class DevInfoReportFormatter(
  private val context: Context,
  private val textCreator: DevInfoTextCreator,
) {
  fun format(tabs: List<TabData>): String = buildString {
    appendLine("# Device Info Report")
    appendLine()
    tabs.forEach { tab ->
      appendLine("## ${context.getString(tab.type.featureTitle)}")
      tab.items.forEach { appendItem(it, indent = 0) }
      appendLine()
    }
  }

  private fun StringBuilder.appendItem(item: Item, indent: Int) {
    val pad = "  ".repeat(indent)
    when (item) {
      is RawTextItem -> appendLine("$pad${item.label}: ${item.value}")
      is TextItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${item.value}")
      is StatusItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${item.value}")
      is DateItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${textCreator.longDateFormat(item.value)}")
      is ElapsedTimeItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${textCreator.dateElapsedFormat(item.value)}")
      is PermissionItem -> appendLine("$pad${item.label}: ${item.value}")
      is DeviceCardItem -> appendLine("$pad${item.androidName} (${item.internalCodename}) — API ${item.sdkVersion}")
      is GroupItem -> {
        item.rawTitle?.let { appendLine("$pad[$it]") }
          ?: item.label?.let { appendLine("$pad[${textCreator.itemLabel(it)}]") }
        item.items.forEach { appendItem(it, indent + 1) }
      }
      is ExpandableGroupItem -> {
        appendLine("$pad[${item.title}] ${item.summary}")
        item.items.forEach { appendItem(it, indent + 1) }
      }
    }
  }
}
