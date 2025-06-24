package com.ryccoatika.sqatools.devinfo.ui

import com.ryccoatika.sqatools.devinfo.inject.DevInfoScope
import com.ryccoatika.sqatools.devinfo.ui.main.Main
import me.tatarka.inject.annotations.Inject

@DevInfoScope
@Inject
internal class FeatureScreens(
  val main: Main,
)
