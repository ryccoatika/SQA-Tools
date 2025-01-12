package com.ryccoatika.sqatools.fillstorage.ui.manage.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ryccoatika.sqatools.common.ui.VerticalSpace
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun FillStorageProgress(
  progress: FillStorage.Progress,
  onDismissRequest: () -> Unit,
) {
  val textCreator = LocalTextCreator.current

  Dialog(
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false,
    ),
    onDismissRequest = onDismissRequest,
  ) {
    Surface(
      shape = MaterialTheme.shapes.extraLarge,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .padding(16.dp)
          .width(IntrinsicSize.Max),
      ) {
        Box(
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator(
            progress = { progress.progress },
            modifier = Modifier.size(96.dp),
          )
          Text(
            text = textCreator.percentageText(progress.progress),
            style = MaterialTheme.typography.titleLarge,
          )
        }
        16.VerticalSpace()
        Text(
          text = textCreator.fillStorageProgress(progress),
        )
        8.VerticalSpace()
        progress.error?.let { error ->
          Text(
            text = textCreator.errorMessage(error),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )
          8.VerticalSpace()
        }
        Button(
          onClick = onDismissRequest,
          shape = MaterialTheme.shapes.large,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(textCreator.fillStorageProgressButtonText(progress))
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun FillStorageProgressPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      FillStorageProgress(
        progress = FillStorage.Progress(
          progress = 0.5f,
          mbFilled = 4096.0,
          mbFill = 8192.0,
          isSuccess = false,
          error = IllegalArgumentException("Hello World"),
        ),
        onDismissRequest = {},
      )
    }
  }
}
