package com.ryccoatika.sqatools.ui.home

import com.ryccoatika.sqatools.common.SQAFeature

internal data class HomeViewState(
  val features: Set<SQAFeature>,
) {
  companion object {
    val Empty = HomeViewState(
      features = emptySet(),
    )
  }
}
