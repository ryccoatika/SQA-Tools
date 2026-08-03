package com.ryccoatika.sqatoolkit.devinfo.ui.common

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalPermissionRequester
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview

@Composable
internal fun ItemPermissionComposer(
  item: PermissionItem,
  modifier: Modifier = Modifier,
) {
  val requester = LocalPermissionRequester.current

  ListItem(
    modifier = modifier,
    headlineContent = { Text(item.label) },
    supportingContent = { Text(item.value) },
    trailingContent = {
      TextButton(onClick = { requester(item.permission) }) {
        Text(stringResource(R.string.di_text_grant))
      }
    },
  )
}

@Preview
@Composable
private fun ItemPermissionComposerPreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      ItemPermissionComposer(
        item = PermissionItem(
          label = "Camera",
          permission = "android.permission.CAMERA",
          value = "Granted",
        ),
      )
    }
  }
}
