package com.ryccoatika.sqatoolkit.fillmemory.ui.home.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.pressable
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.preview.CompositionLocalProviderForPreview

private const val GridColumns = 3

@Composable
internal fun FillMemoryOptions(
  onFill: (FillMemory) -> Unit,
  enabled: Boolean = true,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  val options = remember {
    buildList {
      val mbValues = listOf(
        1.0, 5.0, 10.0,
        15.0, 20.0, 25.0,
        50.0, 75.0, 100.0,
        500.0, 750.0, 1000.0,
      )
      mbValues.forEach { value ->
        add(
          FillMemory(
            value = value,
          ),
        )
      }
    }
  }

  // Rendered as a static (non-lazy) grid: the option list is small and fixed,
  // and this composable is embedded inside a verticalScroll()'d Column at the
  // Home level — a LazyVerticalGrid there would crash with an "infinite height
  // constraints" measurement error, so a plain Column/Row grid is used instead.
  Column(
    verticalArrangement = Arrangement.spacedBy(7.dp),
    modifier = modifier,
  ) {
    options.chunked(GridColumns).forEach { rowOptions ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        rowOptions.forEach { option ->
          FillMemoryOptionChip(
            text = textCreator.fillMemoryButtonText(option),
            enabled = enabled,
            onClick = {
              onFill(option)
            },
            modifier = Modifier.weight(1f),
          )
        }
      }
    }
  }
}

@Composable
private fun FillMemoryOptionChip(
  text: String,
  enabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val accentColor = FeatureAccent.Memory.base()
  val containerColor = if (enabled) {
    accentColor.copy(alpha = 0.15f)
  } else {
    MaterialTheme.colorScheme.surfaceVariant
  }
  val contentColor = if (enabled) {
    accentColor
  } else {
    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
  }

  Surface(
    shape = MaterialTheme.shapes.medium,
    color = containerColor,
    contentColor = contentColor,
    modifier = if (enabled) {
      modifier.pressable(onClick = onClick)
    } else {
      modifier
    },
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 12.dp),
    ) {
      Text(text = text)
    }
  }
}

@PreviewLightDark
@Composable
private fun FillMemoryOptionsPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      FillMemoryOptions(
        onFill = {},
      )
    }
  }
}
