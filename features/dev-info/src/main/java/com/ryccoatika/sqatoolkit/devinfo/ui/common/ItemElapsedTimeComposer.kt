package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.model.ElapsedTimeItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
internal fun ItemElapsedTimeComposer(
  item: ElapsedTimeItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  ListItemText(
    modifier = modifier,
    label = textCreator.itemLabel(item.label),
    value = textCreator.dateElapsedFormat(item.value),
  )
}

@Preview
@Composable
private fun ItemElapsedTimeComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemElapsedTimeComposer(
        item = ElapsedTimeItem(
          label = Label.ManufacturedDate,
          value = Instant.now().minus(1234567890L, ChronoUnit.MILLIS),
        ),
      )
    }
  }
}
