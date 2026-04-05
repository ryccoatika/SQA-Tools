package com.ryccoatika.sqatoolkit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.ryccoatika.sqatoolkit.ui.settings.Settings

internal sealed class RootScreen(val route: String) {
  data object Tools : RootScreen("tools")
  data object ChatBot : RootScreen("chat-bot")
  data object Settings : RootScreen("settings")
}

private sealed class Screen(
  private val route: String,
) {
  fun createRoute(root: RootScreen) = "${root.route}/$route"

  data object Tools : Screen("tools")
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
    startDestination = RootScreen.Tools.route,
    modifier = modifier,
  ) {
    addToolsTopLevel(
      appScreens = appScreens,
    )
    addChatBotTopLevel()
    addSettingsTopLevel(
      appScreens = appScreens,
    )
  }
}

// -------- TOOLS --------
private fun NavGraphBuilder.addToolsTopLevel(
  appScreens: AppScreens,
  root: RootScreen = RootScreen.Tools,
) {
  navigation(
    route = root.route,
    startDestination = Screen.Tools.createRoute(root),
  ) {
    addTools(
      root = root,
      appScreens = appScreens,
    )
  }
}

private fun NavGraphBuilder.addTools(
  root: RootScreen,
  appScreens: AppScreens,
) {
  composable(
    route = Screen.Tools.createRoute(root),
  ) {
    appScreens.tools()
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
