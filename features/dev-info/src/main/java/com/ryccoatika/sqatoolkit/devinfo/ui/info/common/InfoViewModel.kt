package com.ryccoatika.sqatoolkit.devinfo.ui.info.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.utils.ObservableLoadingCounter
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Note: this base intentionally does NOT call refresh() in its own init.
// If it did, refresh() (and load()) would run before a subclass constructor
// finishes assigning its injected usecase field, causing a null-usecase
// crash. Each concrete subclass must call refresh() in its OWN init block.
internal abstract class InfoViewModel : ViewModel() {
  private val items = MutableStateFlow<List<Item>>(emptyList())
  private val loadingCounter = ObservableLoadingCounter()

  val state: StateFlow<InfoViewState> = combine(
    items,
    loadingCounter.observable,
    ::InfoViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = InfoViewState.Empty,
  )

  fun refresh() {
    viewModelScope.launch {
      loadingCounter.addLoader()
      items.value = load()
      loadingCounter.removeLoader()
    }
  }

  protected abstract suspend fun load(): List<Item>
}
