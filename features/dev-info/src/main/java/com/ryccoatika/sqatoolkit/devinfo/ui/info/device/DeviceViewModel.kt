package com.ryccoatika.sqatoolkit.devinfo.ui.info.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.utils.ObservableLoadingCounter
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetDeviceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class DeviceViewModel(
  private val getDeviceInfo: GetDeviceInfo,
) : ViewModel() {
  private val items = MutableStateFlow<List<Item>>(emptyList())
  private val loadingCounter = ObservableLoadingCounter()

  val state: StateFlow<DeviceViewState> = combine(
    items,
    loadingCounter.observable,
    ::DeviceViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DeviceViewState.Empty,
  )

  init {
    viewModelScope.launch {
      loadingCounter.addLoader()
      items.value = getDeviceInfo.executeSync(Unit)
      loadingCounter.removeLoader()
    }
  }
}
