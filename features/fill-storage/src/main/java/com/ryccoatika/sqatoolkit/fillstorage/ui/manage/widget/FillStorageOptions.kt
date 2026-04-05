package com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget

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
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview

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
        Text(text = textCreator.fillStorageButtonText(option))
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
