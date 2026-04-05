package com.ryccoatika.sqatoolkit.devinfo.ui

import com.ryccoatika.sqatoolkit.devinfo.inject.DevInfoScope
import com.ryccoatika.sqatoolkit.devinfo.ui.main.Main
import me.tatarka.inject.annotations.Inject

@DevInfoScope
@Inject
internal class FeatureScreens(
  val main: Main,
)
