package com.ryccoatika.sqatoolkit.fillstorage

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillstorage.core.utils.FillStorageTextCreator
import com.ryccoatika.sqatoolkit.fillstorage.inject.FillStorageScope
import com.ryccoatika.sqatoolkit.fillstorage.ui.FeatureScreens
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.LocalTextCreator
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
        LocalTextCreator provides FillStorageTextCreator(this),
      ) {
        SQAToolsTheme {
          NavHost(
            navController = navController,
            startDestination = Route.Home.route,
          ) {
            composable(
              route = Route.Home.route,
            ) {
              component.screens.home(
                { path ->
                  // openManageStorage
                  navController.navigate(Route.Manage.createRoute(path))
                },
                {
                  // navigateUp
                  finish()
                },
              )
            }
            composable(
              route = Route.Manage.route,
              arguments = listOf(
                navArgument("path") { type = NavType.StringType },
              ),
            ) {
              component.screens.manage(
                {
                  // navigateUp
                  navController.navigateUp()
                },
                { path ->
                  // openDummyFiles
                  navController.navigate(Route.DummyFiles.createRoute(path))
                },
              )
            }
            composable(
              route = Route.DummyFiles.route,
              arguments = listOf(
                navArgument("path") { type = NavType.StringType },
              ),
            ) {
              component.screens.dummyFiles(
                navController::navigateUp, // navigateUp
              )
            }
          }
        }
      }
    }
  }

  sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Manage : Route("manage/{path}") {
      fun createRoute(path: String): String {
        val encodedPath = Uri.encode(path)
        return "manage/$encodedPath"
      }
    }

    data object DummyFiles : Route("dummy-files/{path}") {
      fun createRoute(path: String): String {
        val encodedPath = Uri.encode(path)
        return "dummy-files/$encodedPath"
      }
    }
  }
}

@FillStorageScope
@Component
internal abstract class FillStorageComponent(
  @get:Provides val context: Context,
) {
  abstract val screens: FeatureScreens
}
