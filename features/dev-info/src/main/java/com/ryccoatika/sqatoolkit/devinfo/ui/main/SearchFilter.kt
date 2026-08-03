package com.ryccoatika.sqatoolkit.devinfo.ui.main

import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem

internal fun filterItems(items: List<Item>, query: String): List<Item> {
  if (query.isBlank()) return items
  val q = query.trim()
  return items.mapNotNull { filterItem(it, q) }
}

private fun filterItem(item: Item, q: String): Item? = when (item) {
  is RawTextItem -> item.takeIf { it.label.contains(q, true) || it.value.contains(q, true) }
  is PermissionItem -> item.takeIf { it.label.contains(q, true) || it.value.contains(q, true) }
  is TextItem -> item.takeIf { it.value.contains(q, true) }
  is GroupItem -> {
    val titleMatch = item.rawTitle?.contains(q, true) == true
    if (titleMatch) item else {
      val kids = item.items.mapNotNull { filterItem(it, q) }
      if (kids.isEmpty()) null else item.copy(items = kids)
    }
  }
  is ExpandableGroupItem -> {
    val headMatch = item.title.contains(q, true) || item.summary.contains(q, true)
    if (headMatch) item else {
      val kids = item.items.mapNotNull { filterItem(it, q) }
      if (kids.isEmpty()) null else item.copy(items = kids)
    }
  }
  else -> null
}
