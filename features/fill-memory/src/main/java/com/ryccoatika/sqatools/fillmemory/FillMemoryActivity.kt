package com.ryccoatika.sqatools.fillmemory

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.fillmemory.inject.FillMemoryScope
import com.ryccoatika.sqatools.fillmemory.ui.FeatureScreens
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class FillMemoryActivity : ComponentActivity() {
  private lateinit var component: FillMemoryComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    component = FillMemoryComponent::class.create(this)

    enableEdgeToEdge()

    setContent {
      val navController = rememberNavController()

      SQAToolsTheme {
        NavHost(
          navController = navController,
          startDestination = Route.Home.route,
        ) {
          composable(
            route = Route.Home.route,
          ) {
            component.screens.home()
          }
        }
      }
    }
  }

  sealed class Route(val route: String) {
    data object Home : Route("home")
  }
}

@FillMemoryScope
@Component
internal abstract class FillMemoryComponent(
  @get:Provides val context: Context,
) {
  abstract val screens: FeatureScreens
}
