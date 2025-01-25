package com.ryccoatika.sqatools.devinfo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatools.devinfo.inject.DevInfoScope
import com.ryccoatika.sqatools.devinfo.ui.FeatureScreens
import com.ryccoatika.sqatools.devinfo.ui.common.utils.LocalTextCreator
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

internal class DevInfoActivity : ComponentActivity() {
  private lateinit var component: DevInfoComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    component = DevInfoComponent::class.create(this)

    enableEdgeToEdge()

    setContent {
      val navController = rememberNavController()

      CompositionLocalProvider(
        LocalTextCreator provides DevInfoTextCreator(this),
      ) {
        SQAToolsTheme {
          NavHost(
            navController = navController,
            startDestination = Route.Home.route,
          ) {
            composable(
              route = Route.Home.route,
            ) {
              component.screens.home {
                // navigateUp
                finish()
              }
            }
          }
        }
      }
    }
  }

  sealed class Route(val route: String) {
    data object Home : Route("home")
  }
}

@DevInfoScope
@Component
internal abstract class DevInfoComponent(
  @get:Provides val context: Context,
) {
  abstract val screens: FeatureScreens
}
