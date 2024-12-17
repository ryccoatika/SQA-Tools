package com.ryccoatika.sqatools.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatools.ui.theme.SQAToolsTheme

@Composable
internal fun Home() {
  Scaffold { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {
    }
  }
}

@Preview
@Composable
private fun HomePreview() {
  SQAToolsTheme {
    Home()
  }
}
