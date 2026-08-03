package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun ItemExpandableGroupComposer(
  item: ExpandableGroupItem,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Column(modifier = modifier) {
    ListItem(
      modifier = Modifier.clickable { expanded = !expanded },
      headlineContent = { Text(item.title) },
      supportingContent = { Text(item.summary) },
      trailingContent = {
        Icon(
          imageVector = if (expanded) {
            Icons.Filled.KeyboardArrowUp
          } else {
            Icons.Filled.KeyboardArrowDown
          },
          contentDescription = null,
        )
      },
    )
    AnimatedVisibility(visible = expanded) {
      ItemComposer(items = item.items)
    }
  }
}

@Preview
@Composable
private fun ItemExpandableGroupComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemExpandableGroupComposer(
        item = ExpandableGroupItem(
          title = "Title",
          summary = "Summary",
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
