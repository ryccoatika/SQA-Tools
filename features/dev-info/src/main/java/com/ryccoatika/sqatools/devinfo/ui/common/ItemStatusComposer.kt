package com.ryccoatika.sqatools.devinfo.ui.common

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.devinfo.R
import com.ryccoatika.sqatools.devinfo.core.model.Label
import com.ryccoatika.sqatools.devinfo.core.model.StatusItem
import com.ryccoatika.sqatools.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun ItemStatusComposer(
  item: StatusItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  ListItem(
    modifier = modifier,
    headlineContent = {
      Text(textCreator.itemLabel(item.label))
    },
    supportingContent = {
      if (item.value) {
        Text(text = stringResource(R.string.di_text_supported))
      } else {
        Text(text = stringResource(R.string.di_text_not_supported))
      }
    },
  )
}

@Preview
@Composable
private fun ItemStatusComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemStatusComposer(
        item = StatusItem(
          label = Label.ESim,
          value = false,
        ),
      )
    }
  }
}
