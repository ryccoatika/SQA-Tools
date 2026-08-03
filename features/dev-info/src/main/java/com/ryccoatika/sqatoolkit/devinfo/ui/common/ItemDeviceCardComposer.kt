package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview
import java.time.Instant

@Composable
internal fun AndroidVersionImage(
  sdkVersion: Int,
  modifier: Modifier = Modifier,
) {
  val imageResource = when (sdkVersion) {
    24, 25 -> R.drawable.nougat
    26, 27 -> R.drawable.oreo
    28 -> R.drawable.pie
    29 -> R.drawable.android10
    30 -> R.drawable.android11
    31, 32 -> R.drawable.android12
    33 -> R.drawable.android13
    34 -> R.drawable.android14
    35 -> R.drawable.android15
    36 -> R.drawable.android16
    else -> null
  }
  imageResource?.let {
    Image(
      painter = painterResource(imageResource),
      contentDescription = null,
      modifier = modifier,
    )
  }
}

@Composable
internal fun ItemDeviceCardComposer(
  item: DeviceCardItem,
  modifier: Modifier = Modifier,
) {
  val textCreator = LocalTextCreator.current

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ),
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
      AndroidVersionImage(
        sdkVersion = item.sdkVersion,
        modifier = Modifier.size(72.dp),
      )
      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Text(
          text = textCreator.androidNameAndCodename(item.androidName, item.internalCodename),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        HeroSubLine(value = textCreator.androidApiLevel(item.sdkVersion))
        HeroSubLine(value = textCreator.androidReleaseDate(item.releaseDate))
      }
    }
  }
}

@Composable
private fun HeroSubLine(
  value: String,
) {
  Text(
    text = value,
    style = MaterialTheme.typography.labelLarge,
    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
  )
}

@Preview
@Composable
private fun ItemDeviceCardComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemDeviceCardComposer(
        item = DeviceCardItem(
          androidName = "Android 15",
          internalCodename = "Vanilla Ice Cream",
          sdkVersion = 35,
          releaseDate = Instant.parse("2024-09-03T09:53:32-07:00"),
        ),
      )
    }
  }
}
