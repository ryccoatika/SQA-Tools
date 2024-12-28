package com.ryccoatika.sqatools.fillstorage.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryccoatika.sqatools.common.ui.VerticalSpace
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.StoragePreviewParameterProvider

@Composable
internal fun StorageChart(
  storage: Storage,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current
  Card(
    modifier = modifier,
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
    ) {
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = textCreator.storageTypeTitle(storage.type),
          fontSize = 14.sp,
        )
        Text(
          text = textCreator.storageCapacityDetailText(storage),
          fontSize = 14.sp,
        )
      }
      16.VerticalSpace()
      StorageBarChart(
        storage = storage,
      )
    }
  }
}

@Composable
private fun StorageBarChart(
  storage: Storage,
  barHeight: Dp = 30.dp,
) {
  val textCreator = LocalTextCreator.current
  val density = LocalDensity.current
  var barWidth by remember { mutableFloatStateOf(0f) }
  Box(
    modifier = Modifier
      .height(barHeight)
      .clip(MaterialTheme.shapes.small)
      .fillMaxWidth()
      .onSizeChanged { size ->
        barWidth = with(density) { size.width.toDp().value }
      },
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Gray),
    )
    Row(
      modifier = Modifier.fillMaxSize(),
    ) {
      val usedSpaceWidth = storage.usedSpace * barWidth / storage.totalSpace
      Box(
        modifier = Modifier
          .width(usedSpaceWidth.dp)
          .fillMaxHeight()
          .background(Color.Green),
      )
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxSize(),
      ) {
        val freeSpaceText = textCreator.storageFreeSpaceText(storage)
        val textMeasurer = rememberTextMeasurer()
        val textWidth = with(density) {
          textMeasurer.measure(freeSpaceText).size.width.toDp()
        }
        if (textWidth < (barWidth - usedSpaceWidth).dp) {
          Text(
            text = freeSpaceText,
            color = Color.White,
            fontSize = 12.sp,
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun StorageChartPreview(
  @PreviewParameter(StoragePreviewParameterProvider::class) storage: Storage,
) {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      StorageChart(storage)
    }
  }
}
