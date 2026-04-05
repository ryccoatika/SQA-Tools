package com.ryccoatika.sqatoolkit.devinfo.inject

import com.ryccoatika.sqatoolkit.common.SQAFeature
import com.ryccoatika.sqatoolkit.devinfo.DevInfoFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface DevInfoFeature {
  @Provides
  @IntoSet
  fun provideDevInfoFeature(bind: DevInfoFeature): SQAFeature = bind
}
