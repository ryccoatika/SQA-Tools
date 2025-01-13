package com.ryccoatika.sqatools.fillmemory.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.tatarka.inject.annotations.Inject

typealias Home = @Composable () -> Unit

@Inject
@Composable
internal fun Home() {
  Scaffold { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {
    }
  }
}
