package com.ryccoatika.sqatoolkit.fillstorage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.fillstorage.core.usecase.ObserveStorages
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
internal class HomeViewModel(
  observeStorages: ObserveStorages,
) : ViewModel() {
  val state: StateFlow<HomeViewState> = observeStorages.flow
    .map(::HomeViewState)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = HomeViewState.Empty,
    )

  init {
    observeStorages(Unit)
  }
}
