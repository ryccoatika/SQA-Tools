package com.ryccoatika.sqatools.devinfo.ui

import com.ryccoatika.sqatools.devinfo.inject.DevInfoScope
import com.ryccoatika.sqatools.devinfo.ui.home.Home
import me.tatarka.inject.annotations.Inject

@DevInfoScope
@Inject
internal class FeatureScreens(
  val home: Home,
)
