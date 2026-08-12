package com.ryccoatika.sqatoolkit.fillmemory.ui

import com.ryccoatika.sqatoolkit.fillmemory.inject.FillMemoryScope
import com.ryccoatika.sqatoolkit.fillmemory.ui.home.Home
import me.tatarka.inject.annotations.Inject

@FillMemoryScope
@Inject
internal class FeatureScreens(
  val home: Home,
)
