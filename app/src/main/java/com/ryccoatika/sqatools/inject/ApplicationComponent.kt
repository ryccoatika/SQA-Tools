package com.ryccoatika.sqatools.inject

import android.app.Application
import android.content.Context
import com.ryccoatika.sqatools.SQAToolsApplication
import com.ryccoatika.sqatools.common.inject.ApplicationScope
import com.ryccoatika.sqatools.fillstorage.inject.FillStorageFeature
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class ApplicationComponent(
  @get:Provides val application: Application,
) : FillStorageFeature {
  companion object {
    fun from(context: Context): ApplicationComponent {
      return (context.applicationContext as SQAToolsApplication).component
    }
  }
}
