package com.ryccoatika.sqatools

import com.ryccoatika.sqatools.ui.settings.Settings
import com.ryccoatika.sqatools.ui.tools.Tools
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreens(
  val tools: Tools,
  val settings: Settings,
)
