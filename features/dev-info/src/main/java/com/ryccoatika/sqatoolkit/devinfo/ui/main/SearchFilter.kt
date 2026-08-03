package com.ryccoatika.sqatoolkit.devinfo.ui.main

import com.ryccoatika.sqatoolkit.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.itemPlainText

internal fun filterItems(
  items: List<Item>,
  query: String,
  textCreator: DevInfoTextCreator,
  supportedText: String,
  notSupportedText: String,
): List<Item> {
  if (query.isBlank()) return items
  val q = query.trim()
  return items.mapNotNull { filterItem(it, q, textCreator, supportedText, notSupportedText) }
}

private fun filterItem(
  item: Item,
  q: String,
  textCreator: DevInfoTextCreator,
  supportedText: String,
  notSupportedText: String,
): Item? = when (item) {
  is DeviceCardItem -> item.takeIf {
    it.androidName.contains(q, true) || it.internalCodename.contains(q, true)
  }
  is GroupItem -> {
    val titleMatch = item.rawTitle?.contains(q, true) == true
    if (titleMatch) {
      item
    } else {
      val kids = item.items.mapNotNull { filterItem(it, q, textCreator, supportedText, notSupportedText) }
      if (kids.isEmpty()) null else item.copy(items = kids)
    }
  }
  is ExpandableGroupItem -> {
    val headMatch = item.title.contains(q, true) || item.summary.contains(q, true)
    if (headMatch) {
      item
    } else {
      val kids = item.items.mapNotNull { filterItem(it, q, textCreator, supportedText, notSupportedText) }
      if (kids.isEmpty()) null else item.copy(items = kids)
    }
  }
  else -> {
    val plain = itemPlainText(item, textCreator, supportedText, notSupportedText)
    item.takeIf { plain?.contains(q, true) == true }
  }
}
