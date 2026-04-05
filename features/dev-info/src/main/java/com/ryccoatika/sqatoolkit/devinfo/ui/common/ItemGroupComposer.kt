package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun ItemGroupComposer(
  item: GroupItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  Card(
    modifier = modifier,
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(8.dp),
    ) {
      item.label?.let { label ->
        Text(textCreator.itemLabel(label))
      }
      ItemComposer(
        items = item.items,
        verticalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.clip(MaterialTheme.shapes.small),
      )
    }
  }
}

@Preview
@Composable
private fun ItemGroupComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemGroupComposer(
        item = GroupItem(
          label = Label.Device,
          items = listOf(
            TextItem(
              label = Label.Device,
              value = "value",
            ),
          ),
        ),
      )
    }
  }
}
