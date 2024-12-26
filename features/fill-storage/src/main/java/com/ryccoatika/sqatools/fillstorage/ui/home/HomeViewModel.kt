package com.ryccoatika.sqatools.fillstorage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.usecase.ObserveStorages
import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class HomeViewModel(
  private val observeStorages: ObserveStorages,
) : ViewModel() {
  private val metric = MutableStateFlow(Storage.Metric.GB)

  val state = combine(
    metric,
    observeStorages.flow,
  ) { metric, storages ->
    HomeViewState(
      metric = metric,
      storages = storages,
    )
  }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = HomeViewState.Empty,
    )

  init {
    metric
      .onEach { updateDataSource() }
      .launchIn(viewModelScope)
  }

  fun updateMetric(metric: Storage.Metric) {
    this.metric.value = metric
  }

  private fun updateDataSource() {
    observeStorages(
      ObserveStorages.Params(
        metric = metric.value,
      ),
    )
  }
}
