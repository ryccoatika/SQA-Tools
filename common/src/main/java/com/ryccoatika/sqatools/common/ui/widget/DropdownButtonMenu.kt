package com.ryccoatika.sqatools.common.ui.widget

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun <T> DropdownButtonMenu(
  text: String,
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
    modifier = modifier,
  ) {
    TextButton(
      onClick = {
        showMenu = !showMenu
      },
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
