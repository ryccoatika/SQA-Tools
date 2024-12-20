package com.ryccoatika.sqatools.fillstorage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme

internal class FillStorageActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SQAToolsTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          FillStorageContent(
            modifier = Modifier.padding(innerPadding),
          )
        }
      }
    }
  }
}

@Composable
private fun FillStorageContent(modifier: Modifier = Modifier) {
  Text(
    text = "Fill Storage",
    modifier = modifier,
  )
}

@Preview
@Composable
private fun FillStorageContentPreview() {
  SQAToolsTheme {
    FillStorageContent()
  }
}
