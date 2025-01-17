plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.ryccoatika.sqatools.fillmemory"

  buildFeatures {
    compose = true
  }

  externalNativeBuild {
    cmake {
      path = file("src/main/cpp/CMakeLists.txt")
      version = "3.31.1"
    }
  }
}

dependencies {
  implementation(projects.common)

  implementation(libs.androidx.core.ktx)

  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  ksp(libs.kotlininject.compiler)

  implementation(libs.androidx.navigationCompose)
}
