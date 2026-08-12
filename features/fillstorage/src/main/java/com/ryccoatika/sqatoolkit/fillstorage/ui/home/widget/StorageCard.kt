package com.ryccoatika.sqatoolkit.fillstorage.ui.home.widget

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryccoatika.sqatoolkit.common.ui.AnimatedCountText
import com.ryccoatika.sqatoolkit.common.ui.VerticalSpace
import com.ryccoatika.sqatoolkit.common.ui.animatedFraction
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.usageStatusColor
import com.ryccoatika.sqatoolkit.fillstorage.R
import com.ryccoatika.sqatoolkit.fillstorage.core.model.Storage
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.StoragePreviewParameterProvider

@Composable
internal fun StorageCard(
  storage: Storage,
  onManageButtonClicked: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current
  val gbTemplate = stringResource(R.string.fs_text_gb_value)
  val capacityTemplate = stringResource(R.string.fs_desc_storage_capacity)
  val accentColor = FeatureAccent.Storage.base()

  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ),
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
          text = textCreator.storageTitle(storage),
          fontSize = 14.sp,
        )
        AnimatedCountText(
          value = storage.capacityInGB.usedSpace.toFloat(),
          formatter = { used ->
            val usedText = String.format(gbTemplate, used)
            val totalText = String.format(gbTemplate, storage.capacityInGB.totalSpace)
            String.format(capacityTemplate, usedText, totalText)
          },
          style = LocalTextStyle.current.copy(fontSize = 14.sp),
        )
      }
      16.VerticalSpace()
      StorageBarChart(
        storage = storage,
      )
      8.VerticalSpace()
      FilledTonalButton(
        onClick = onManageButtonClicked,
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = accentColor.copy(alpha = 0.15f),
          contentColor = accentColor,
        ),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(text = stringResource(R.string.fs_button_manage))
      }
    }
  }
}

@Composable
private fun StorageBarChart(
  storage: Storage,
  barHeight: Dp = 30.dp,
) {
  val textCreator = LocalTextCreator.current
  val gbTemplate = stringResource(R.string.fs_text_gb_value)
  val density = LocalDensity.current
  var barWidth by remember { mutableFloatStateOf(0f) }

  val fraction = with(storage.capacity) {
    if (totalSpace.signum() == 0) {
      0f
    } else {
      usedSpace.toFloat() / totalSpace.toFloat()
    }
  }.coerceIn(0f, 1f)
  val animatedFillFraction by animatedFraction(fraction)
  val usedSpaceColor by animateColorAsState(
    targetValue = usageStatusColor(fraction),
    label = "usedSpaceColor",
  )

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
        .background(MaterialTheme.colorScheme.surfaceVariant),
    )
    Row(
      modifier = Modifier.fillMaxSize(),
    ) {
      val usedSpaceWidth = barWidth * animatedFillFraction
      Box(
        modifier = Modifier
          .width(usedSpaceWidth.dp)
          .fillMaxHeight()
          .background(usedSpaceColor),
      )
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxSize(),
      ) {
        val freeSpaceText = textCreator.storageFreeSpace(storage)
        val textMeasurer = rememberTextMeasurer()
        val textWidth = with(density) {
          textMeasurer.measure(freeSpaceText).size.width.toDp()
        }
        if (textWidth < (barWidth - usedSpaceWidth).dp) {
          AnimatedCountText(
            value = storage.capacityInGB.freeSpace.toFloat(),
            formatter = { free -> String.format(gbTemplate, free) },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = LocalTextStyle.current.copy(fontSize = 12.sp),
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun StorageCardPreview(
  @PreviewParameter(StoragePreviewParameterProvider::class) storage: Storage,
) {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      StorageCard(
        storage = storage,
        onManageButtonClicked = {},
      )
    }
  }
}
