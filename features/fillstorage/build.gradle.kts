plugins {
  alias(libs.plugins.android.dynamic.feature)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.ryccoatika.sqatoolkit.fillstorage"

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(project(":app"))
  implementation(projects.common)

  implementation(libs.androidx.core.ktx)

  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  ksp(libs.kotlininject.compiler)

  implementation(libs.androidx.navigationCompose)
}
