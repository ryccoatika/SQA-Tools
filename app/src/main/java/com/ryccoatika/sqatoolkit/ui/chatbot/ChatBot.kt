package com.ryccoatika.sqatoolkit.ui.chatbot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.widget.EmptyState
import com.ryccoatika.sqatoolkit.common.ui.widget.GradientHero

@Composable
internal fun ChatBot() {
  Scaffold(
    contentWindowInsets = WindowInsets(0),
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      GradientHero(
        title = stringResource(R.string.nav_chat),
        subtitle = stringResource(R.string.chatbot_subtitle),
        accent = FeatureAccent.Neutral,
        applyStatusBarInset = true,
      )
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
      ) {
        EmptyState(
          icon = Icons.Rounded.AutoAwesome,
          title = stringResource(R.string.chatbot_empty_title),
          message = stringResource(R.string.chatbot_empty_message),
        )
      }
    }
  }
}

@Preview
@Composable
private fun ChatBotPreview() {
  SQAToolsTheme {
    ChatBot()
  }
}
