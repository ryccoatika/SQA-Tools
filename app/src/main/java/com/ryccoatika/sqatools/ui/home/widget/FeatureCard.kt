package com.ryccoatika.sqatools.ui.home.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryccoatika.sqatools.common.SQAFeature
import com.ryccoatika.sqatools.common.ui.HorizontalSpace
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.ui.common.utils.preview.FeatureCardPreviewParameterProvider

@Composable
internal fun FeatureCard(
  feature: SQAFeature,
) {
  val context = LocalContext.current
  Card(
    onClick = {
      feature.open(context)
    },
    modifier = Modifier.padding(
      horizontal = 8.dp,
      vertical = 4.dp,
    ),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .padding(
          horizontal = 16.dp,
          vertical = 12.dp,
        ),
    ) {
      Icon(
        imageVector = feature.icon,
        contentDescription = null,
      )
      16.HorizontalSpace()
      Column(
        modifier = Modifier.weight(1f),
      ) {
        Text(
          text = stringResource(id = feature.featureTitle),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = stringResource(id = feature.featureDescription),
          fontSize = 12.sp,
          maxLines = 3,
          lineHeight = 16.sp,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
internal fun FeatureCardPreview(
  @PreviewParameter(FeatureCardPreviewParameterProvider::class)
  feature: SQAFeature,
) {
  SQAToolsTheme {
    FeatureCard(
      feature = feature,
    )
  }
}
