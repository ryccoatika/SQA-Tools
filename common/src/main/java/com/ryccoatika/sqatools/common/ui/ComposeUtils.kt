package com.ryccoatika.sqatools.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Int.HorizontalSpace() {
  Box(Modifier.width(this.dp))
}

@Composable
fun Int.VerticalSpace() {
  Box(Modifier.height(this.dp))
}
