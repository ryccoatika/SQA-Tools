package com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview

private const val GridColumns = 3

@Composable
internal fun FillStorageOptions(
  onFill: (FillStorage) -> Unit,
  enabled: Boolean = true,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  val options = remember {
    buildList {
      val mbValues = listOf(10.0, 50.0, 100.0, 200.0, 250.0, 500.0)
      mbValues.forEach { value ->
        add(
          FillStorage(
            value = value,
            type = FillStorage.Type.MB,
          ),
        )
      }
      val gbValues = listOf(1.0, 5.0, 10.0, 20.0, 25.0, 50.0)
      gbValues.forEach { value ->
        add(
          FillStorage(
            value = value,
            type = FillStorage.Type.GB,
          ),
        )
      }
      val percentValues = listOf(25.0, 50.0, 90.0)
      percentValues.forEach { value ->
        add(
          FillStorage(
            value = value,
            type = FillStorage.Type.PERCENT,
          ),
        )
      }
    }
  }

  // Rendered as a static (non-lazy) grid: the option list is small and fixed,
  // and this composable is embedded inside a verticalScroll()'d Column at the
  // Manage level — a LazyVerticalGrid there would crash with an "infinite
  // height constraints" measurement error, so a plain Column/Row grid is used
  // instead.
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
          Button(
            shape = MaterialTheme.shapes.medium,
            enabled = enabled,
            onClick = {
              onFill(option)
            },
            modifier = Modifier.weight(1f),
          ) {
            Text(text = textCreator.fillStorageButtonText(option))
          }
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun FillStorageOptionsPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      FillStorageOptions(
        onFill = {},
      )
    }
  }
}
