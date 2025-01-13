package com.ryccoatika.sqatools.fillmemory.ui

import com.ryccoatika.sqatools.fillmemory.inject.FillMemoryScope
import com.ryccoatika.sqatools.fillmemory.ui.home.Home
import me.tatarka.inject.annotations.Inject

@FillMemoryScope
@Inject
internal class FeatureScreens(
  val home: Home,
)
