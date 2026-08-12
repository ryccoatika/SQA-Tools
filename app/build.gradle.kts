plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.ryccoatika.sqatoolkit"

  defaultConfig {
    applicationId = "com.ryccoatika.sqatoolkit"

    versionCode = 1
    versionName = "0.6.9-dev01"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(projects.common)
  implementation(projects.features.fillStorage)
  implementation(projects.features.fillMemory)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.navigationCompose)

  implementation(libs.kotlininject.runtime)
  ksp(libs.kotlininject.compiler)
}
