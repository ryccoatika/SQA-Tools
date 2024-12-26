package com.ryccoatika.sqatools

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.ryccoatika.sqatools.ui.home.Home
import com.ryccoatika.sqatools.ui.settings.Settings

internal sealed class RootScreen(val route: String) {
  data object Home : RootScreen("home")
  data object ChatBot : RootScreen("chat-bot")
  data object Settings : RootScreen("settings")
}

private sealed class Screen(
  private val route: String,
) {
  fun createRoute(root: RootScreen) = "${root.route}/$route"

  data object Home : Screen("home")
  data object ChatBot : Screen("chat-bot")
  data object Settings : Screen("settings")
}

@Composable
internal fun AppNavigation(
  navController: NavHostController,
  appScreens: AppScreens,
  modifier: Modifier = Modifier,
) {
  NavHost(
    navController = navController,
    startDestination = RootScreen.Home.route,
    modifier = modifier,
  ) {
    addHomeTopLevel(
      appScreens = appScreens,
    )
    addChatBotTopLevel()
    addSettingsTopLevel(
      appScreens = appScreens,
    )
  }
}

// -------- HOME --------
private fun NavGraphBuilder.addHomeTopLevel(
  appScreens: AppScreens,
  root: RootScreen = RootScreen.Home,
) {
  navigation(
    route = root.route,
    startDestination = Screen.Home.createRoute(root),
  ) {
    addHome(
      root = root,
      home = appScreens.home,
    )
  }
}

private fun NavGraphBuilder.addHome(
  root: RootScreen,
  home: Home,
) {
  composable(
    route = Screen.Home.createRoute(root),
  ) {
    home()
  }
}

// -------- CHAT-BOT --------
private fun NavGraphBuilder.addChatBotTopLevel(
  root: RootScreen = RootScreen.ChatBot,
) {
  navigation(
    route = root.route,
    startDestination = Screen.ChatBot.createRoute(root),
  ) {
    addChatBot(root)
  }
}

private fun NavGraphBuilder.addChatBot(
  root: RootScreen,
) {
  composable(
    route = Screen.ChatBot.createRoute(root),
  ) {
//    ChatBot()
  }
}

// -------- SETTINGS --------
private fun NavGraphBuilder.addSettingsTopLevel(
  appScreens: AppScreens,
  root: RootScreen = RootScreen.Settings,
) {
  navigation(
    route = root.route,
    startDestination = Screen.Settings.createRoute(root),
  ) {
    addSettings(
      root = root,
      settings = appScreens.settings,
    )
  }
}

private fun NavGraphBuilder.addSettings(
  root: RootScreen,
  settings: Settings,
) {
  composable(
    route = Screen.Settings.createRoute(root),
  ) {
    settings()
  }
}
