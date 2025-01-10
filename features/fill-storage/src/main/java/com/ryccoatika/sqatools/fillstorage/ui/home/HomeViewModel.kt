package com.ryccoatika.sqatools.fillstorage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.usecase.ObserveStorages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
internal class HomeViewModel(
  observeStorages: ObserveStorages,
) : ViewModel() {
  private val metric = MutableStateFlow(Storage.Metric.GB)

  val state: StateFlow<HomeViewState> = combine(
    metric,
    observeStorages.flow,
  ) { metric, storages ->
    HomeViewState(
      metric = metric,
      storages = storages.map { it.convert(metric) },
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = HomeViewState.Empty,
  )

  init {
    observeStorages(Unit)
  }

  fun updateMetric(metric: Storage.Metric) {
    this.metric.value = metric
  }
}
