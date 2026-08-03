package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import android.content.ClipData
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.MonoValueTextStyle
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalSnackbarHostState
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.statusColorFor
import kotlinx.coroutines.launch

@Composable
internal fun ListItemText(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  val clipboard = LocalClipboard.current
  val snackbar = LocalSnackbarHostState.current
  val scope = rememberCoroutineScope()
  val statusColor = statusColorFor(value)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        scope.launch {
          clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(label, value)))
          snackbar.showSnackbar("Copied $label")
        }
      }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = value,
        style = MonoValueTextStyle,
        color = statusColor ?: MaterialTheme.colorScheme.onSurface,
      )
    }
    Icon(
      imageVector = Icons.Rounded.ContentCopy,
      contentDescription = "Copy",
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
      modifier = Modifier.size(18.dp),
    )
  }
}

@Preview
@Composable
private fun ListItemTextPreview() {
  CompositionLocalProviderForPreview {
    CompositionLocalProvider(LocalSnackbarHostState provides SnackbarHostState()) {
      SQAToolsTheme {
        ListItemText(
          label = "Device",
          value = "Samsung",
        )
      }
    }
  }
}
