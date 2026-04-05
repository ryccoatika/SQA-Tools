package com.ryccoatika.sqatoolkit.fillstorage.inject

import com.ryccoatika.sqatoolkit.common.SQAFeature
import com.ryccoatika.sqatoolkit.fillstorage.FillStorageFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface FillStorageFeature {
  @Provides
  @IntoSet
  fun provideFillStorageFeature(bind: FillStorageFeature): SQAFeature = bind
}
