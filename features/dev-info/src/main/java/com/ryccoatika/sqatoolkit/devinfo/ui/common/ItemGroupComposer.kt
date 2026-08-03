package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import android.content.ClipData
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalSnackbarHostState
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview
import kotlinx.coroutines.launch

@Composable
internal fun ItemGroupComposer(
  item: GroupItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current
  val clipboard = LocalClipboard.current
  val snackbar = LocalSnackbarHostState.current
  val scope = rememberCoroutineScope()

  val title = item.rawTitle ?: item.label?.let { textCreator.itemLabel(it) }.orEmpty()

  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = groupIcon(title),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp),
        )
        Text(
          text = title.uppercase(),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f),
        )
        val supportedText = stringResource(R.string.di_text_supported)
        val notSupportedText = stringResource(R.string.di_text_not_supported)
        val copyText = groupItemsToText(item.items, textCreator, supportedText, notSupportedText)
        if (copyText.isNotBlank()) {
          IconButton(
            onClick = {
              scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(title, copyText)))
                snackbar.showSnackbar("Copied $title")
              }
            },
            modifier = Modifier.size(24.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.ContentCopy,
              contentDescription = "Copy group",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }
      item.items.forEachIndexed { index, child ->
        if (index > 0) {
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          )
        }
        ItemComposer(child)
      }
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
          rawTitle = "Processor",
          items = listOf(
            RawTextItem(label = "Chipset", value = "Snapdragon"),
            RawTextItem(label = "Cores", value = "8"),
          ),
        ),
      )
    }
  }
}
