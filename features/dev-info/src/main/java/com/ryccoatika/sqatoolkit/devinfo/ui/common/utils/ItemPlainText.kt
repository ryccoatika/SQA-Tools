package com.ryccoatika.sqatoolkit.devinfo.ui.common.utils

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

/**
 * Renders a single row to the same "label: value" plain text the UI/report shows.
 * Returns null for [GroupItem]/[ExpandableGroupItem] — callers own the recursion into
 * their children so headers aren't duplicated.
 *
 * Shared by the search matcher ([com.ryccoatika.sqatoolkit.devinfo.ui.main.filterItems]),
 * the group-copy button ([com.ryccoatika.sqatoolkit.devinfo.ui.common.groupItemsToText]),
 * and the share report formatter to keep the three in sync.
 */
internal fun itemPlainText(
  item: Item,
  textCreator: DevInfoTextCreator,
  supportedText: String,
  notSupportedText: String,
): String? = when (item) {
  is RawTextItem -> "${item.label}: ${item.value}"
  is PermissionItem -> "${item.label}: ${item.value}"
  is TextItem -> "${textCreator.itemLabel(item.label)}: ${item.value}"
  is StatusItem -> {
    val statusText = if (item.value) supportedText else notSupportedText
    "${textCreator.itemLabel(item.label)}: $statusText"
  }
  is DateItem -> {
    val dateText = if (item.isPeriod) {
      textCreator.datePeriodFormat(item.value)
    } else {
      textCreator.longDateFormat(item.value)
    }
    "${textCreator.itemLabel(item.label)}: $dateText"
  }
  is ElapsedTimeItem -> "${textCreator.itemLabel(item.label)}: ${textCreator.dateElapsedFormat(item.value)}"
  is DeviceCardItem -> "${item.androidName} (${item.internalCodename}) — API ${item.sdkVersion}"
  is GroupItem -> null
  is ExpandableGroupItem -> null
}
