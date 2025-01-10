package com.ryccoatika.sqatools.fillstorage.ui.manage.widget

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
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun FillStorageOptions(
  onFill: (FillStorage) -> Unit,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  val options = remember {
    buildList {
      val mbValues = listOf(10f, 50f, 100f, 200f, 250f, 500f)
      mbValues.forEach { value ->
        add(
          FillStorage(
            value = value,
            type = FillStorage.Type.MB,
          ),
        )
      }
      val gbValues = listOf(1f, 5f, 10f, 20f, 25f, 50f)
      gbValues.forEach { value ->
        add(
          FillStorage(
            value = value,
            type = FillStorage.Type.GB,
          ),
        )
      }
      val percentValues = listOf(25f, 50f, 90f)
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
