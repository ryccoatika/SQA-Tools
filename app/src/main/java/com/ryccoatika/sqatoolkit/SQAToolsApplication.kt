package com.ryccoatika.sqatoolkit

import android.app.Application
import com.ryccoatika.sqatoolkit.common.extensions.unsafeLazy
import com.ryccoatika.sqatoolkit.inject.ApplicationComponent
import com.ryccoatika.sqatoolkit.inject.create

class SQAToolsApplication : Application() {
  val component: ApplicationComponent by unsafeLazy { ApplicationComponent::class.create(this) }
}
