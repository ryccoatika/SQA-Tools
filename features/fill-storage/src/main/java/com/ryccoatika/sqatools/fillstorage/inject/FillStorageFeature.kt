package com.ryccoatika.sqatools.fillstorage.inject

import com.ryccoatika.sqatools.common.SQAFeature
import com.ryccoatika.sqatools.fillstorage.FillStorageFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface FillStorageFeature {
  @Provides
  @IntoSet
  fun provideFillStorageFeature(bind: FillStorageFeature): SQAFeature = bind
}
