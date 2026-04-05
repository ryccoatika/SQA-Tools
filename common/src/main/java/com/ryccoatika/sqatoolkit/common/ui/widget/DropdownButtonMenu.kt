package com.ryccoatika.sqatoolkit.common.ui.widget

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme

@Composable
fun <T> DropdownButtonMenu(
  text: String,
  buttonType: DropdownButtonMenuType = DropdownButtonMenuType.FilledButton,
  options: List<T>,
  optionText: (T) -> String,
  onSelected: (T) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showMenu by remember { mutableStateOf(false) }

  val transition = updateTransition(targetState = showMenu, label = "DropdownMenu")
  val rotation by transition.animateFloat(
    label = "IconRotation",
    transitionSpec = { tween() },
  ) { isShow ->
    if (isShow) 180f else 0f
  }

  Box(
    modifier = modifier.height(IntrinsicSize.Max),
  ) {
    DropdownButton(
      type = buttonType,
      onClick = {
        showMenu = !showMenu
      },
      modifier = Modifier.fillMaxHeight(),
    ) {
      Text(
        text = text,
      )
      Icon(
        imageVector = Icons.Rounded.ArrowDropDown,
        contentDescription = null,
        modifier = Modifier.rotate(rotation),
      )
    }
    DropdownMenu(
      expanded = showMenu,
      onDismissRequest = {
        showMenu = false
      },
    ) {
      options.forEach { option ->
        DropdownMenuItem(
          text = {
            Text(
              text = optionText(option),
            )
          },
          onClick = {
            onSelected(option)
            showMenu = false
          },
        )
      }
    }
  }
}

@Composable
private fun DropdownButton(
  type: DropdownButtonMenuType,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  when (type) {
    DropdownButtonMenuType.FilledButton -> {
      Button(
        onClick = onClick,
        content = content,
        modifier = modifier,
      )
    }

    DropdownButtonMenuType.TextButton -> {
      TextButton(
        onClick = onClick,
        content = content,
        modifier = modifier,
      )
    }
    DropdownButtonMenuType.OutlinedButton -> {
      OutlinedButton(
        onClick = onClick,
        content = content,
        modifier = modifier,
      )
    }
  }
}

enum class DropdownButtonMenuType {
  FilledButton,
  TextButton,
  OutlinedButton,
}

@PreviewLightDark
@Composable
private fun DropdownButtonMenuPreview() {
  SQAToolsTheme {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      DropdownButtonMenu(
        text = "Text",
        buttonType = DropdownButtonMenuType.FilledButton,
        options = listOf("Option 1", "Option 2"),
        optionText = { it },
        onSelected = {},
      )
      DropdownButtonMenu(
        text = "Text",
        buttonType = DropdownButtonMenuType.OutlinedButton,
        options = listOf("Option 1", "Option 2"),
        optionText = { it },
        onSelected = {},
      )
      DropdownButtonMenu(
        text = "Text",
        buttonType = DropdownButtonMenuType.TextButton,
        options = listOf("Option 1", "Option 2"),
        optionText = { it },
        onSelected = {},
      )
    }
  }
}
