package com.ryccoatika.sqatools.fillmemory.ui.home.floater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.common.utils.ObservableLoadingCounter
import com.ryccoatika.sqatools.common.utils.collectStatus
import com.ryccoatika.sqatools.fillmemory.core.model.FillMemory as FillMemoryModel
import com.ryccoatika.sqatools.fillmemory.core.usecase.ClearMemory
import com.ryccoatika.sqatools.fillmemory.core.usecase.FillMemory
import com.ryccoatika.sqatools.fillmemory.core.usecase.ObserveMemoryUsage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class FloaterFillMemoryViewModel(
  observeMemoryUsage: ObserveMemoryUsage,
  private val fillMemory: FillMemory,
  private val clearMemory: ClearMemory,
) : ViewModel() {
  private val loadingState = ObservableLoadingCounter()

  val state: StateFlow<FloaterFillMemoryViewState> = combine(
    observeMemoryUsage.flow,
    fillMemory.progress,
    loadingState.observable,
    ::FloaterFillMemoryViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = FloaterFillMemoryViewState.Empty,
  )

  init {
    observeMemoryUsage(Unit)
  }

  fun fillMemory(fillMemory: FillMemoryModel) {
    this.fillMemory.cancel()
    fillMemory(FillMemory.Params(fillMemory))
  }

  fun clearMemory() {
    fillMemory.cancel()
    viewModelScope.launch {
      clearMemory(Unit).collectStatus(loadingState)
    }
  }
}
