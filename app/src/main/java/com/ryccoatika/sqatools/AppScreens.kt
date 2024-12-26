package com.ryccoatika.sqatools

import com.ryccoatika.sqatools.ui.home.Home
import com.ryccoatika.sqatools.ui.settings.Settings
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreens(
  val home: Home,
  val settings: Settings,
)
