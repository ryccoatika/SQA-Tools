package com.ryccoatika.sqatools.fillstorage.di

import com.ryccoatika.sqatools.common.SQAFeature
import com.ryccoatika.sqatools.fillstorage.FillStorageFeature
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface FillStorageComponent {
  @Provides
  @IntoSet
  fun provideFillStorageComponent(bind: FillStorageFeature): SQAFeature = bind
}
