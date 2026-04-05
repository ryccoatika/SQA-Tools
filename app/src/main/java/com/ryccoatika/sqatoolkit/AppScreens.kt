package com.ryccoatika.sqatoolkit

import com.ryccoatika.sqatoolkit.ui.settings.Settings
import com.ryccoatika.sqatoolkit.ui.tools.Tools
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreens(
  val tools: Tools,
  val settings: Settings,
)
