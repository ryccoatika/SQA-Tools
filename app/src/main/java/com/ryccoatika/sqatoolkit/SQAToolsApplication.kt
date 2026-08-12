package com.ryccoatika.sqatoolkit

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.ryccoatika.sqatoolkit.common.extensions.unsafeLazy
import com.ryccoatika.sqatoolkit.inject.ApplicationComponent
import com.ryccoatika.sqatoolkit.inject.create

class SQAToolsApplication : SplitCompatApplication() {
  val component: ApplicationComponent by unsafeLazy { ApplicationComponent::class.create(this) }
}
