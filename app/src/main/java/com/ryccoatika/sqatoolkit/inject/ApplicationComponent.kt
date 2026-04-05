package com.ryccoatika.sqatoolkit.inject

import android.app.Application
import android.content.Context
import com.ryccoatika.sqatoolkit.SQAToolsApplication
import com.ryccoatika.sqatoolkit.common.inject.ApplicationScope
import com.ryccoatika.sqatoolkit.devinfo.inject.DevInfoFeature
import com.ryccoatika.sqatoolkit.fillmemory.inject.FillMemoryFeature
import com.ryccoatika.sqatoolkit.fillstorage.inject.FillStorageFeature
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class ApplicationComponent(
  @get:Provides val application: Application,
) : FillStorageFeature,
  FillMemoryFeature,
  DevInfoFeature {
  companion object {
    fun from(context: Context): ApplicationComponent {
      return (context.applicationContext as SQAToolsApplication).component
    }
  }
}
