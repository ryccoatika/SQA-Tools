package com.ryccoatika.sqatoolkit.feature

import android.content.Context
import android.content.Intent
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class FeatureInstallManager(context: Context) {
  private val manager: SplitInstallManager =
    SplitInstallManagerFactory.create(context.applicationContext)

  private val states = MutableStateFlow(
    FeatureRegistry.features.associate { descriptor ->
      descriptor.moduleName to if (descriptor.moduleName in manager.installedModules) {
        FeatureInstallState.Installed
      } else {
        FeatureInstallState.NotInstalled
      }
    },
  )
  val statesFlow: StateFlow<Map<String, FeatureInstallState>> = states.asStateFlow()

  // Pending user-confirmation session, surfaced to the UI so it can launch Play's dialog.
  private val pendingConfirmationState = MutableStateFlow<SplitInstallSessionState?>(null)
  val pendingConfirmation: StateFlow<SplitInstallSessionState?> = pendingConfirmationState.asStateFlow()

  private val listener = SplitInstallStateUpdatedListener { state ->
    val name = state.moduleNames().firstOrNull() ?: return@SplitInstallStateUpdatedListener
    val next = when (state.status()) {
      SplitInstallSessionStatus.PENDING -> FeatureInstallState.Downloading(0f)

      SplitInstallSessionStatus.DOWNLOADING -> {
        val total = state.totalBytesToDownload().coerceAtLeast(1L)
        FeatureInstallState.Downloading(state.bytesDownloaded().toFloat() / total)
      }

      SplitInstallSessionStatus.INSTALLING -> FeatureInstallState.Installing

      SplitInstallSessionStatus.INSTALLED -> FeatureInstallState.Installed

      SplitInstallSessionStatus.FAILED -> FeatureInstallState.Failed("Install failed (${state.errorCode()})")

      SplitInstallSessionStatus.CANCELED -> FeatureInstallState.NotInstalled

      SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
        pendingConfirmationState.value = state
        FeatureInstallState.Downloading(0f)
      }

      else -> return@SplitInstallStateUpdatedListener
    }
    states.update { it + (name to next) }
  }

  fun register() = manager.registerListener(listener)

  fun unregister() = manager.unregisterListener(listener)

  fun install(moduleName: String) {
    if (moduleName in manager.installedModules) {
      states.update { it + (moduleName to FeatureInstallState.Installed) }
      return
    }
    states.update { it + (moduleName to FeatureInstallState.Downloading(0f)) }
    val request = SplitInstallRequest.newBuilder().addModule(moduleName).build()
    manager.startInstall(request).addOnFailureListener { e ->
      states.update { it + (moduleName to FeatureInstallState.Failed(e.message ?: "Install failed")) }
    }
  }

  fun uninstall(moduleName: String) {
    manager.deferredUninstall(listOf(moduleName))
    states.update { it + (moduleName to FeatureInstallState.NotInstalled) }
  }

  fun consumeConfirmation() {
    pendingConfirmationState.value = null
  }

  fun launch(descriptor: FeatureDescriptor, context: Context) {
    // Ensure freshly installed split code/resources are available to this process.
    SplitCompat.install(context)
    val intent = Intent(context, Class.forName(descriptor.activityFqn))
    context.startActivity(intent)
  }
}
