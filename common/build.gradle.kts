plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.ryccoatika.sqatools.common"

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)

  implementation(libs.androidx.lifecycle.viewmodel.compose)

  api(platform(libs.androidx.compose.bom))
  api(libs.androidx.material3)
  api(libs.androidx.ui)
  api(libs.androidx.ui.graphics)
  api(libs.androidx.ui.tooling.preview)
  api(libs.androidx.materialIcons)

  debugApi(libs.androidx.ui.tooling)
  debugApi(libs.androidx.ui.test.manifest)

  api(libs.kotlininject.runtime)
  ksp(libs.kotlininject.compiler)
}
