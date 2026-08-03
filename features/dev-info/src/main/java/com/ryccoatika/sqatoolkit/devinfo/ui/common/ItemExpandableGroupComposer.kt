package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
  val chevronRotation by animateFloatAsState(
    targetValue = if (expanded) 180f else 0f,
    label = "chevronRotation",
  )

  Column(modifier = modifier) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = !expanded }
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = item.summary,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Icon(
        imageVector = Icons.Rounded.ExpandMore,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.rotate(chevronRotation),
      )
    }
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
