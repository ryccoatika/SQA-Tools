package com.ryccoatika.sqatoolkit.devinfo.ui.main

import android.content.Context
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.itemPlainText
import me.tatarka.inject.annotations.Inject

// Lives in ui.main (same package as TabData) — do not move to core/.
@Inject
internal class DevInfoReportFormatter(
  private val context: Context,
  private val textCreator: DevInfoTextCreator,
) {
  private val supportedText by lazy { context.getString(R.string.di_text_supported) }
  private val notSupportedText by lazy { context.getString(R.string.di_text_not_supported) }

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
      is GroupItem -> {
        item.rawTitle?.let { appendLine("$pad[$it]") }
          ?: item.label?.let { appendLine("$pad[${textCreator.itemLabel(it)}]") }
        item.items.forEach { appendItem(it, indent + 1) }
      }
      is ExpandableGroupItem -> {
        appendLine("$pad[${item.title}] ${item.summary}")
        item.items.forEach { appendItem(it, indent + 1) }
      }
      else -> {
        itemPlainText(item, textCreator, supportedText, notSupportedText)?.let {
          appendLine("$pad$it")
        }
      }
    }
  }
}
