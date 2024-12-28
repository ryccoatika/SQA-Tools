package com.ryccoatika.sqatools.fillstorage

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.fillstorage.core.utils.FillStorageTextCreator
import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import com.ryccoatika.sqatools.fillstorage.ui.FeatureScreens
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

internal class FillStorageActivity : ComponentActivity() {
  private lateinit var component: FillStorageComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    component = FillStorageComponent::class.create(this)

    enableEdgeToEdge()

    setContent {
      val navController = rememberNavController()

      CompositionLocalProvider(
        LocalTextCreator provides component.textCreator,
      ) {
        SQAToolsTheme {
          Scaffold(
            modifier = Modifier.fillMaxSize(),
          ) { paddingValues ->
            NavHost(
              navController = navController,
              startDestination = Route.Home.route,
              modifier = Modifier.padding(paddingValues),
            ) {
              composable(Route.Home.route) {
                component.screens.home {
                  finish()
                }
              }
            }
          }
        }
      }
    }
  }

  enum class Route(val route: String) {
    Home("home"),
  }
}

@FillStorageScope
@Component
internal abstract class FillStorageComponent(
  @get:Provides val activity: Activity,
) {
  abstract val screens: FeatureScreens
  abstract val textCreator: FillStorageTextCreator
}
