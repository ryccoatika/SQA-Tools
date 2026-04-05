package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun ItemTextComposer(
  item: TextItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  ListItem(
    modifier = modifier,
    headlineContent = {
      Text(textCreator.itemLabel(item.label))
    },
    supportingContent = {
      Text(item.value)
    },
  )
}

@Preview
@Composable
private fun ItemTextComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemTextComposer(
        item = TextItem(
          label = Label.Device,
          value = "Samsung",
        ),
      )
    }
  }
}
