package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.model.DateItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
internal fun ItemDateComposer(
  item: DateItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  ListItem(
    modifier = modifier,
    headlineContent = {
      Text(textCreator.itemLabel(item.label))
    },
    supportingContent = {
      if (item.isPeriod) {
        Text(textCreator.datePeriodFormat(item.value))
      } else {
        Text(textCreator.longDateFormat(item.value))
      }
    },
  )
}

@Preview
@Composable
private fun ItemDateComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      Column {
        ItemDateComposer(
          item = DateItem(
            label = Label.ManufacturedDate,
            value = Instant.now(),
          ),
        )
        ItemDateComposer(
          item = DateItem(
            label = Label.ManufacturedDate,
            value = Instant.now().minus(794, ChronoUnit.DAYS),
            isPeriod = true,
          ),
        )
      }
    }
  }
}
