package com.ryccoatika.sqatoolkit.ui.tools.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.ui.HorizontalSpace
import com.ryccoatika.sqatoolkit.common.ui.animatedFraction
import com.ryccoatika.sqatoolkit.common.ui.pressable
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.feature.FeatureDescriptor
import com.ryccoatika.sqatoolkit.feature.FeatureInstallState
import com.ryccoatika.sqatoolkit.ui.common.utils.preview.FeatureCardPreviewParameterProvider

@Composable
internal fun FeatureCard(
  descriptor: FeatureDescriptor,
  state: FeatureInstallState,
  onDownload: () -> Unit,
  onOpen: () -> Unit,
  onRemove: () -> Unit,
) {
  val noop: () -> Unit = {}
  // Card-body tap only fires the safe action (open); starting a download/retry must go
  // through the explicit button so an incidental tap can't kick off a network+storage install.
  val primaryAction: () -> Unit = when (state) {
    FeatureInstallState.Installed -> onOpen
    FeatureInstallState.NotInstalled,
    is FeatureInstallState.Downloading,
    FeatureInstallState.Installing,
    is FeatureInstallState.Failed,
    -> noop
  }
  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    modifier = Modifier
      .fillMaxWidth()
      .pressable(onClick = primaryAction),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(16.dp),
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(44.dp)
          .clip(MaterialTheme.shapes.medium)
          .background(descriptor.accent.base().copy(alpha = 0.16f)),
      ) {
        Icon(
          imageVector = descriptor.icon,
          contentDescription = null,
          tint = descriptor.accent.base(),
        )
      }
      16.HorizontalSpace()
      Column(
        modifier = Modifier.weight(1f),
      ) {
        Text(
          text = stringResource(id = descriptor.title),
          style = MaterialTheme.typography.titleMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = stringResource(id = descriptor.description),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
        )
      }
      16.HorizontalSpace()
      FeatureCardAction(
        state = state,
        onDownload = onDownload,
        onOpen = onOpen,
        onRemove = onRemove,
      )
    }
  }
}

@Composable
private fun FeatureCardAction(
  state: FeatureInstallState,
  onDownload: () -> Unit,
  onOpen: () -> Unit,
  onRemove: () -> Unit,
) {
  when (state) {
    FeatureInstallState.NotInstalled -> {
      FilledTonalButton(onClick = onDownload) {
        Text(text = stringResource(R.string.feature_download))
      }
    }

    is FeatureInstallState.Downloading -> {
      val animatedProgress by animatedFraction(state.progress)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp),
      ) {
        LinearProgressIndicator(
          progress = { animatedProgress },
          modifier = Modifier.fillMaxWidth(),
        )
        Text(
          text = stringResource(R.string.feature_percent, (state.progress * 100).toInt()),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    FeatureInstallState.Installing -> {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp))
        8.HorizontalSpace()
        Text(
          text = stringResource(R.string.feature_installing),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    FeatureInstallState.Installed -> {
      Row(verticalAlignment = Alignment.CenterVertically) {
        FilledTonalButton(onClick = onOpen) {
          Text(text = stringResource(R.string.feature_open))
        }
        var menuExpanded by remember { mutableStateOf(false) }
        IconButton(onClick = { menuExpanded = true }) {
          Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = { menuExpanded = false },
        ) {
          DropdownMenuItem(
            text = { Text(text = stringResource(R.string.feature_remove)) },
            onClick = {
              menuExpanded = false
              onRemove()
            },
          )
        }
      }
    }

    is FeatureInstallState.Failed -> {
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = state.message,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.error,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        TextButton(onClick = onDownload) {
          Text(text = stringResource(R.string.feature_retry))
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
internal fun FeatureCardPreview(
  @PreviewParameter(FeatureCardPreviewParameterProvider::class)
  state: FeatureInstallState,
) {
  SQAToolsTheme {
    FeatureCard(
      descriptor = com.ryccoatika.sqatoolkit.ui.common.utils.preview.previewFeatureDescriptor,
      state = state,
      onDownload = {},
      onOpen = {},
      onRemove = {},
    )
  }
}
