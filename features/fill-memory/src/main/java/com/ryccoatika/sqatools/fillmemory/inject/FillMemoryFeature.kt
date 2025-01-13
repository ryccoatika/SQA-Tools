package com.ryccoatika.sqatools.fillmemory.inject

import com.ryccoatika.sqatools.common.SQAFeature
import com.ryccoatika.sqatools.fillmemory.FillMemoryFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface FillMemoryFeature {
  @Provides
  @IntoSet
  fun provideFillStorageFeature(bind: FillMemoryFeature): SQAFeature = bind
}
