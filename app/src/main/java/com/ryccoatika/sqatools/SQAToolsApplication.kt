package com.ryccoatika.sqatools

import android.app.Application
import com.ryccoatika.sqatools.common.extensions.unsafeLazy
import com.ryccoatika.sqatools.inject.ApplicationComponent
import com.ryccoatika.sqatools.inject.create

class SQAToolsApplication : Application() {
  val component: ApplicationComponent by unsafeLazy { ApplicationComponent::class.create(this) }
}
