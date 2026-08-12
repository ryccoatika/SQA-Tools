import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.dynamic.feature) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.spotless)
}

allprojects {
  // apply spotless plugin on all modules
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
  spotless {
    kotlin {
      target("**/*.kt")
      targetExclude(
        "${layout.buildDirectory.get().asFile}/**/*.kt",
      )
      ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
      target("**/*.kts")
      targetExclude(
        "${layout.buildDirectory.get().asFile}/**/*.kts",
      )
      ktlint(libs.versions.ktlint.get())
    }
  }

  // Configure Java to use our chosen language level. Kotlin will automatically
  // pick this up
  plugins.withType<JavaBasePlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
      toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
      }
    }
  }

  tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
      // Treat all Kotlin warnings as errors
      allWarningsAsErrors.set(true)

      // Enable experimental coroutines APIs, including Flow
      freeCompilerArgs.addAll(
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.FlowPreview",
      )
    }
  }

  pluginManager.withPlugin("com.android.application") {
    extensions.configure<ApplicationExtension> {
      configureAndroid(this)
      defaultConfig.targetSdk = 36
    }
    addDesugaring()
  }
  pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension> {
      configureAndroid(this)
      testOptions.targetSdk = 36
    }
    addDesugaring()
  }
  pluginManager.withPlugin("com.android.dynamic-feature") {
    extensions.configure<DynamicFeatureExtension> {
      configureAndroid(this)
    }
    addDesugaring()
  }
}

fun configureAndroid(android: CommonExtension) {
  android.apply {
    compileSdk = 37

    defaultConfig.minSdk = 24

    compileOptions.apply {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11

      isCoreLibraryDesugaringEnabled = true
    }

    externalNativeBuild.cmake.version = "4.1.1"
    ndkVersion = "28.2.13676358"
  }
}

fun Project.addDesugaring() {
  dependencies {
    add("coreLibraryDesugaring", libs.desugarJdkLibs)
  }
}
