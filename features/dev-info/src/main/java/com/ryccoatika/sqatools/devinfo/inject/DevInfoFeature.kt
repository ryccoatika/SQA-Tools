package com.ryccoatika.sqatools.devinfo.inject

import com.ryccoatika.sqatools.common.SQAFeature
import com.ryccoatika.sqatools.devinfo.DevInfoFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface DevInfoFeature {
  @Provides
  @IntoSet
  fun provideDevInfoFeature(bind: DevInfoFeature): SQAFeature = bind
}
