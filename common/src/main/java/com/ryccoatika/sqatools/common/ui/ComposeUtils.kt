package com.ryccoatika.sqatools.common.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Int.HorizontalSpace() {
  Spacer(Modifier.width(this.dp))
}

@Composable
fun Int.VerticalSpace() {
  Spacer(Modifier.height(this.dp))
}
