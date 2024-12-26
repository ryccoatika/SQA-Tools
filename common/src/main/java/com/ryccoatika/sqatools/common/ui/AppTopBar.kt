package com.ryccoatika.sqatools.common.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
  title: String,
  actions: @Composable RowScope.() -> Unit = {},
  onBackPressed: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
) {
  TopAppBar(
    title = {
      Text(title)
    },
    navigationIcon = onBackPressed?.let {
      {
        IconButton(
          onClick = onBackPressed,
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null,
          )
        }
      }
    } ?: {},
    actions = actions,
    modifier = modifier,
  )
}
