package com.ryccoatika.sqatools.fillstorage.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.usecase.ObserveStorages
import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class HomeViewModel(
  private val observeStorages: ObserveStorages,
) : ViewModel() {
  private val metrics = MutableStateFlow(Storage.Metrics.MB)

  val state = observeStorages.flow.map { storages ->
    HomeViewState(
      storages = storages,
    )
  }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = HomeViewState.Empty,
    )

  init {
    metrics
      .onEach { updateDataSource() }
      .launchIn(viewModelScope)
  }

  private fun updateDataSource() {
    observeStorages(
      ObserveStorages.Params(
        metrics = metrics.value,
      ),
    )
  }
}
