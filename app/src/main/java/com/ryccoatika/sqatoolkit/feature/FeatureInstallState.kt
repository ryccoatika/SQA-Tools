package com.ryccoatika.sqatoolkit.feature

internal sealed interface FeatureInstallState {
  data object NotInstalled : FeatureInstallState
  data class Downloading(val progress: Float) : FeatureInstallState
  data object Installing : FeatureInstallState
  data object Installed : FeatureInstallState
  data class Failed(val message: String) : FeatureInstallState
}
