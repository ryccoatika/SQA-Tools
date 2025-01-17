package com.ryccoatika.sqatools.fillmemory.ui.home.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatools.fillmemory.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillmemory.ui.common.utils.preview.CompositionLocalProviderForPreview

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

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    horizontalArrangement = Arrangement.spacedBy(7.dp),
    modifier = modifier,
  ) {
    items(
      items = options,
    ) { option ->
      Button(
        shape = MaterialTheme.shapes.medium,
        enabled = enabled,
        onClick = {
          onFill(option)
        },
      ) {
        Text(text = textCreator.fillMemoryButtonText(option))
      }
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
