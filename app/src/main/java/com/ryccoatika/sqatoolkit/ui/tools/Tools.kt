package com.ryccoatika.sqatoolkit.ui.tools

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.feature.FeatureInstallManager
import com.ryccoatika.sqatoolkit.feature.FeatureInstallState
import com.ryccoatika.sqatoolkit.feature.FeatureRegistry
import com.ryccoatika.sqatoolkit.ui.tools.widget.FeatureCard
import me.tatarka.inject.annotations.Inject

internal typealias Tools = @Composable () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
internal fun Tools() {
  val context = LocalContext.current
  val installManager = remember { FeatureInstallManager(context) }
  DisposableEffect(installManager) {
    installManager.register()
    onDispose { installManager.unregister() }
  }
  val states by installManager.statesFlow.collectAsState()

  // Large modules may require a user-confirmation dialog before the download begins.
  val pendingConfirmation by installManager.pendingConfirmation.collectAsState()
  val confirmationLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult(),
  ) {
    // The install listener drives the resulting state; nothing to do with the result here.
  }
  LaunchedEffect(pendingConfirmation) {
    val state = pendingConfirmation ?: return@LaunchedEffect

    // resolutionIntent() is deprecated in feature-delivery but remains the version-stable way
    // to obtain the confirmation IntentSender launched via StartIntentSenderForResult.
    @Suppress("DEPRECATION")
    val resolutionIntent = state.resolutionIntent()
    resolutionIntent?.let { pendingIntent ->
      confirmationLauncher.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
    }
    installManager.consumeConfirmation()
  }

  Scaffold(
    topBar = {
      AppTopBar(title = stringResource(R.string.app_name))
    },
  ) { paddingValues ->
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(paddingValues),
    ) {
      items(FeatureRegistry.features, key = { it.featureId }) { descriptor ->
        FeatureCard(
          descriptor = descriptor,
          state = states[descriptor.moduleName] ?: FeatureInstallState.NotInstalled,
          onDownload = { installManager.install(descriptor.moduleName) },
          onOpen = { installManager.launch(descriptor, context) },
          onRemove = { installManager.uninstall(descriptor.moduleName) },
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun ToolsPreview() {
  SQAToolsTheme {
    Tools()
  }
}
