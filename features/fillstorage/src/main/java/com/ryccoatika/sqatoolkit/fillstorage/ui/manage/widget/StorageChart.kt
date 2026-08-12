package com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.HorizontalSpace
import com.ryccoatika.sqatoolkit.common.ui.VerticalSpace
import com.ryccoatika.sqatoolkit.common.ui.animatedFraction
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.usageStatusColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StorageChart(
  bars: List<ChartBar>,
  label: String? = null,
  labelStyle: TextStyle = MaterialTheme.typography.displayMedium,
  legendStyle: TextStyle = MaterialTheme.typography.labelMedium,
  barWidth: Dp = 16.dp,
  modifier: Modifier = Modifier.fillMaxWidth(),
) {
  val barWidthPx = with(LocalDensity.current) { barWidth.toPx() }
  val trackColor = MaterialTheme.colorScheme.surfaceVariant

  val density = LocalDensity.current
  var containerDpWidth by remember { mutableStateOf(0.dp) }
  val canvasHeight = containerDpWidth.value / 2 + barWidth.value / 2

  val animatedValues = bars.map { animatedFraction(it.value).value }
  val animatedColors = bars.map { bar ->
    animateColorAsState(targetValue = bar.color, label = "chartBarColor").value
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .onSizeChanged {
        containerDpWidth = with(density) { it.width.toDp() }
      },
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .width(containerDpWidth)
        .height(canvasHeight.dp),
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize(),
      ) {
        val center = Offset(size.width / 2, size.height)
        val radius = size.width / 2
        val rectSize = Size(size.width - barWidthPx, radius * 2)
        val topLeft = Offset(center.x - radius + barWidthPx / 2, center.y - radius)

        drawArc(
          color = trackColor,
          startAngle = -180f,
          sweepAngle = 180f,
          useCenter = false,
          topLeft = topLeft,
          size = rectSize,
          style = Stroke(width = barWidthPx),
        )

        var previousStartAngle = 1f
        animatedValues.forEachIndexed { index, value ->
          val startAngle = -180 * previousStartAngle
          val sweepAngle = 180f * value

          previousStartAngle -= value

          drawArc(
            color = animatedColors[index],
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = rectSize,
            style = Stroke(width = barWidthPx),
          )
        }
      }
      if (!label.isNullOrBlank()) {
        Text(
          text = label,
          style = labelStyle,
          modifier = Modifier.padding(top = barWidth * 2),
        )
      }
    }
    8.VerticalSpace()
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      bars.forEach { bar ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            Modifier
              .size(10.dp)
              .background(bar.color, CircleShape),
          )
          5.HorizontalSpace()
          Text(
            text = bar.label,
            style = legendStyle,
            modifier = Modifier,
          )
        }
      }
    }
  }
}

data class ChartBar(
  val value: Float,
  val color: Color,
  val label: String,
)

@PreviewLightDark
@Composable
private fun StorageChartPreview() {
  SQAToolsTheme {
    StorageChart(
      bars = listOf(
        ChartBar(
          value = 0.5f,
          color = usageStatusColor(0.5f),
          label = "Label 1",
        ),
        ChartBar(
          value = 0.2f,
          color = MaterialTheme.colorScheme.tertiary,
          label = "Label 2",
        ),
      ),
      label = "80%",
    )
  }
}
