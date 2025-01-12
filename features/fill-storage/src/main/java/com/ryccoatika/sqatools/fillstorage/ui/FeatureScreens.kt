package com.ryccoatika.sqatools.fillstorage.ui

import com.ryccoatika.sqatools.fillstorage.inject.FillStorageScope
import com.ryccoatika.sqatools.fillstorage.ui.dummyfiles.DummyFiles
import com.ryccoatika.sqatools.fillstorage.ui.home.Home
import com.ryccoatika.sqatools.fillstorage.ui.manage.Manage
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class FeatureScreens(
  val home: Home,
  val manage: Manage,
  val dummyFiles: DummyFiles,
)
