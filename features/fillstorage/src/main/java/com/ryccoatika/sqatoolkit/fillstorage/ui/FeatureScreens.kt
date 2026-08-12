package com.ryccoatika.sqatoolkit.fillstorage.ui

import com.ryccoatika.sqatoolkit.fillstorage.inject.FillStorageScope
import com.ryccoatika.sqatoolkit.fillstorage.ui.dummyfiles.DummyFiles
import com.ryccoatika.sqatoolkit.fillstorage.ui.home.Home
import com.ryccoatika.sqatoolkit.fillstorage.ui.manage.Manage
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class FeatureScreens(
  val home: Home,
  val manage: Manage,
  val dummyFiles: DummyFiles,
)
