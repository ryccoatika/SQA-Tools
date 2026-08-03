package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun ListItemText(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  ListItem(
    modifier = modifier,
    headlineContent = {
      Text(label)
    },
    supportingContent = {
      Text(value)
    },
  )
}

@Preview
@Composable
private fun ListItemTextPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ListItemText(
        label = "Device",
        value = "Samsung",
      )
    }
  }
}
