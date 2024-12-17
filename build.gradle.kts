import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.android.library) apply false
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

  pluginManager.withPlugin("com.android.application") {
    configurePlugin()
  }
  pluginManager.withPlugin("com.android.library") {
    configurePlugin()
  }
}

fun Project.configurePlugin() {
  tasks.withType<KotlinCompile> {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_1_8)
    }
  }

  extensions.configure<BaseExtension> {
    compileSdkVersion(35)

    defaultConfig {
      minSdk = 24
      targetSdk = 35
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
    }
  }
}
