package com.ryccoatika.sqatools.fillstorage.ui.manage.widget

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ryccoatika.sqatools.common.ui.HorizontalSpace
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.common.ui.widget.DropdownButtonMenu
import com.ryccoatika.sqatools.common.ui.widget.DropdownButtonMenuType
import com.ryccoatika.sqatools.fillstorage.R
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage

@Composable
internal fun FillStorageField(
  onFill: (FillStorage) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  var value by remember { mutableStateOf("1.0") }
  var type by remember { mutableStateOf(FillStorage.Type.GB) }

  val errorMessage = getErrorValidation(context, type, value)
  val isError = !errorMessage.isNullOrBlank()

  Row(
    modifier = modifier,
  ) {
    OutlinedTextField(
      value = value,
      onValueChange = { value = it },
      modifier = Modifier.weight(1f),
      isError = isError,
      supportingText = {
        if (isError) {
          Text(errorMessage ?: "")
        }
      },
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
        keyboardType = KeyboardType.Number,
      ),
      trailingIcon = {
        DropdownButtonMenu(
          text = getFillStorageTypeText(context, type),
          buttonType = DropdownButtonMenuType.TextButton,
          options = FillStorage.Type.entries,
          optionText = { getFillStorageTypeText(context, it) },
          onSelected = { type = it },
        )
      },
    )
    8.HorizontalSpace()
    Button(
      onClick = {
        onFill(FillStorage(value = value.toFloatOrNull() ?: 0f, type = type))
      },
      enabled = !isError,
      shape = MaterialTheme.shapes.medium,
      modifier = Modifier.height(TextFieldDefaults.MinHeight),
    ) {
      Text(stringResource(R.string.fs_button_fill))
    }
  }
}

private fun getErrorValidation(
  context: Context,
  type: FillStorage.Type,
  value: String,
): String? {
  if (value.toFloatOrNull() == null) {
    return context.getString(R.string.fs_validation_number_only)
  }

  return when (type) {
    FillStorage.Type.PERCENT -> {
      if (value.toFloat() !in 1.0..100.0) {
        context.getString(R.string.fs_validation_percent_range)
      } else {
        null
      }
    }

    else -> {
      if (value.toFloat() < 1) {
        context.getString(R.string.fs_validation_greater_than_zero)
      } else {
        null
      }
    }
  }
}

private fun getFillStorageTypeText(context: Context, type: FillStorage.Type): String {
  return when (type) {
    FillStorage.Type.MB -> context.getString(R.string.fs_text_mb)
    FillStorage.Type.GB -> context.getString(R.string.fs_text_gb)
    FillStorage.Type.PERCENT -> context.getString(R.string.fs_text_percent)
  }
}

@PreviewLightDark
@Composable
private fun FillStorageFieldPreview() {
  SQAToolsTheme {
    FillStorageField(
      onFill = {},
    )
  }
}
