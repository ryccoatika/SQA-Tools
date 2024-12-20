package com.ryccoatika.sqatools.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.common.SQAFeature
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
internal class HomeViewModel(
  sqaFeatures: Set<SQAFeature>,
) : ViewModel() {
  val state: StateFlow<HomeViewState> = flowOf(
    sqaFeatures,
  ).map {
    HomeViewState(it)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = HomeViewState.Empty,
  )
}
