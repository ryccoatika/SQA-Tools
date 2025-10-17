package com.ryccoatika.sqatools.devinfo.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatools.devinfo.core.model.DateItem
import com.ryccoatika.sqatools.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatools.devinfo.core.model.GroupItem
import com.ryccoatika.sqatools.devinfo.core.model.Item
import com.ryccoatika.sqatools.devinfo.core.model.StatusItem
import com.ryccoatika.sqatools.devinfo.core.model.TextItem

@Composable
internal fun ItemComposer(
  items: List<Item>,
  modifier: Modifier = Modifier,
  verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
) {
  Column(
    modifier = modifier,
    verticalArrangement = verticalArrangement,
  ) {
    items.forEach { ItemComposer(it) }
  }
}

@Composable
internal fun ItemComposer(
  item: Item,
) {
  when (item) {
    is GroupItem -> ItemGroupComposer(item)
    is TextItem -> ItemTextComposer(item)
    is StatusItem -> ItemStatusComposer(item)
    is DateItem -> ItemDateComposer(item)
    is DeviceCardItem -> ItemDeviceCardComposer(item)
  }
}
