package com.ryccoatika.sqatools.fillstorage.ui

import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import com.ryccoatika.sqatools.fillstorage.ui.home.Home
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class FeatureScreens(
  val home: Home,
)
