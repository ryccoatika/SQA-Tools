import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.android) apply false
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
    configurePlugin()
  }
  pluginManager.withPlugin("com.android.library") {
    configurePlugin()
  }
}

fun Project.configurePlugin() {
  extensions.configure<BaseExtension> {
    compileSdkVersion(35)

    defaultConfig {
      minSdk = 24
      targetSdk = 35
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_11
      targetCompatibility = JavaVersion.VERSION_11
    }
  }
}
