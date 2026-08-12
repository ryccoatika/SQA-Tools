package com.ryccoatika.sqatoolkit.fillmemory.ui.home.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.AnimatedCountText
import com.ryccoatika.sqatoolkit.common.ui.VerticalSpace
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.usageStatusColor
import com.ryccoatika.sqatoolkit.common.ui.widget.EmptyState
import com.ryccoatika.sqatoolkit.fillmemory.R
import com.ryccoatika.sqatoolkit.fillmemory.core.model.MemoryUsage
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.preview.MemoryGraphParameterProvider

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MemoryGraph(
  memoryUsages: List<MemoryUsage>,
  modifier: Modifier = Modifier,
) {
  if (memoryUsages.isEmpty()) {
    EmptyState(
      icon = Icons.AutoMirrored.Rounded.ShowChart,
      title = stringResource(R.string.fm_empty_history_title),
      message = stringResource(R.string.fm_empty_history_message),
      modifier = modifier,
    )
    return
  }

  val density = LocalDensity.current
  val textCreator = LocalTextCreator.current
  val lastMemoryUsage = memoryUsages.last()
  val totalTemplate = stringResource(R.string.fm_text_memory_total)
  val usedTemplate = stringResource(R.string.fm_text_memory_used)
  val freeTemplate = stringResource(R.string.fm_text_memory_free)

  var canvasHeight by remember { mutableStateOf(0.dp) }
  var canvasHeightPx by remember { mutableFloatStateOf(0f) }

  val fraction = lastMemoryUsage.usedMemoryPercent / 100f
  val graphColor by animateColorAsState(
    targetValue = usageStatusColor(fraction),
    label = "graphColor",
  )
  val xStepWidth = 2.dp

  Column(
    modifier = modifier.width(IntrinsicSize.Max),
  ) {
    Box(
      contentAlignment = Alignment.TopEnd,
    ) {
      Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 100.dp)
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
          .horizontalScroll(rememberScrollState(), reverseScrolling = true)
          .onSizeChanged {
            canvasHeight = with(density) { it.height.toDp() }
            canvasHeightPx = it.height.toFloat()
          },
      ) {
        Canvas(
          modifier = Modifier
            .height(canvasHeight)
            .width(xStepWidth * memoryUsages.size),
        ) {
          val pathYRatio = size.height / lastMemoryUsage.totalMemory.toLong()

          val path = Path()
          var xPosition = 0f

          path.moveTo(x = 0f, y = size.height)
          memoryUsages.forEach { usage ->
            xPosition += xStepWidth.toPx()
            val yPosition = size.height - (usage.usedMemory.toLong() * pathYRatio)
            path.lineTo(x = xPosition, y = yPosition)
          }
          path.lineTo(x = xPosition, y = size.height)
          path.close()

          drawPath(path, graphColor, 0.5f)
          drawPath(path, graphColor, style = Stroke(1.dp.toPx()))
        }
      }
      AnimatedCountText(
        value = fraction,
        formatter = textCreator::percentText,
        modifier = Modifier.padding(4.dp),
      )
    }
    4.VerticalSpace()
    CompositionLocalProvider(
      LocalTextStyle provides MaterialTheme.typography.labelLarge,
    ) {
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth(),
      ) {
        AnimatedCountText(
          value = lastMemoryUsage.totalMemoryInMB.toFloat(),
          formatter = { value -> String.format(totalTemplate, value) },
        )
        AnimatedCountText(
          value = lastMemoryUsage.usedMemoryInMB.toFloat(),
          formatter = { value -> String.format(usedTemplate, value) },
        )
        AnimatedCountText(
          value = lastMemoryUsage.availableMemoryInMB.toFloat(),
          formatter = { value -> String.format(freeTemplate, value) },
        )
        Text(
          text = textCreator.memoryDummyText(lastMemoryUsage),
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun MemoryGraphPreview(
  @PreviewParameter(MemoryGraphParameterProvider::class) memoryUsages: List<MemoryUsage>,
) {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      MemoryGraph(
        memoryUsages = memoryUsages,
        modifier = Modifier.width(300.dp),
      )
    }
  }
}
