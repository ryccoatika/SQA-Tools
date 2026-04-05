package com.ryccoatika.sqatoolkit.fillmemory.inject

import com.ryccoatika.sqatoolkit.common.SQAFeature
import com.ryccoatika.sqatoolkit.fillmemory.FillMemoryFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface FillMemoryFeature {
  @Provides
  @IntoSet
  fun provideFillStorageFeature(bind: FillMemoryFeature): SQAFeature = bind
}
