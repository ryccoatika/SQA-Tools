package com.ryccoatika.sqatoolkit.fillmemory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.utils.ObservableLoadingCounter
import com.ryccoatika.sqatoolkit.common.utils.collectStatus
import com.ryccoatika.sqatoolkit.fillmemory.core.model.FillMemory as FillMemoryModel
import com.ryccoatika.sqatoolkit.fillmemory.core.model.MemoryUsage
import com.ryccoatika.sqatoolkit.fillmemory.core.usecase.ClearMemory
import com.ryccoatika.sqatoolkit.fillmemory.core.usecase.FillMemory
import com.ryccoatika.sqatoolkit.fillmemory.core.usecase.ObserveMemoryUsage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class HomeViewModel(
  observeMemoryUsage: ObserveMemoryUsage,
  private val fillMemory: FillMemory,
  private val clearMemory: ClearMemory,
) : ViewModel() {
  private val memoryUsageHistory = MutableStateFlow<List<MemoryUsage>>(emptyList())
  private val loadingState = ObservableLoadingCounter()

  val state: StateFlow<HomeViewState> = combine(
    memoryUsageHistory,
    observeMemoryUsage.flow,
    fillMemory.progress,
    loadingState.observable,
    ::HomeViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = HomeViewState.Empty,
  )

  init {
    observeMemoryUsage(Unit)

    viewModelScope.launch {
      observeMemoryUsage.flow.collect { memoryUsage ->
        memoryUsageHistory.value += memoryUsage
      }
    }
  }

  fun fillMemory(fillMemory: FillMemoryModel) {
    fillMemory(FillMemory.Params(fillMemory))
  }

  fun clearMemory() {
    viewModelScope.launch {
      clearMemory(Unit).collectStatus(loadingState)
    }
  }

  fun dismissProgress() {
    fillMemory.cancel()
  }
}
