package com.ryccoatika.sqatoolkit.devinfo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetCameraInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetConnectivityInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetDeviceInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetHardwareInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetNetworkInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSensorInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSoftwareInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class DevInfoViewModel(
  types: Set<DevInfoType>,
  getDeviceInfo: GetDeviceInfo,
  getSoftwareInfo: GetSoftwareInfo,
  getHardwareInfo: GetHardwareInfo,
  getCameraInfo: GetCameraInfo,
  getNetworkInfo: GetNetworkInfo,
  getConnectivityInfo: GetConnectivityInfo,
  getSensorInfo: GetSensorInfo,
) : ViewModel() {
  private val useCases: Map<String, ResultInteractor<Unit, List<Item>>> = mapOf(
    DevInfoType.DEVICE_ID to getDeviceInfo,
    DevInfoType.SOFTWARE_ID to getSoftwareInfo,
    DevInfoType.HARDWARE_ID to getHardwareInfo,
    DevInfoType.CAMERA_ID to getCameraInfo,
    DevInfoType.NETWORK_ID to getNetworkInfo,
    DevInfoType.CONNECTIVITY_ID to getConnectivityInfo,
    DevInfoType.SENSOR_ID to getSensorInfo,
  )

  private val orderedTypes = types.sortedBy { it.order }
  private val itemsByType = MutableStateFlow<Map<String, List<Item>>>(emptyMap())
  private val loadingIds = MutableStateFlow(orderedTypes.map { it.id }.toSet())
  private val query = MutableStateFlow("")

  val state: StateFlow<DevInfoViewState> = combine(
    itemsByType,
    loadingIds,
    query,
  ) { items, loading, q ->
    DevInfoViewState(
      tabs = orderedTypes.map { type ->
        TabData(
          type = type,
          items = items[type.id].orEmpty(),
          isLoading = type.id in loading,
        )
      },
      query = q,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DevInfoViewState.Empty,
  )

  init {
    orderedTypes.forEach { load(it.id) }
  }

  fun refresh(type: DevInfoType) = load(type.id)

  fun refreshAll() = orderedTypes.forEach { load(it.id) }

  fun onQueryChange(q: String) {
    query.value = q
  }

  private fun load(id: String) {
    val useCase = useCases[id] ?: return
    viewModelScope.launch {
      loadingIds.update { it + id }
      val result = useCase.executeSync(Unit)
      itemsByType.update { it + (id to result) }
      loadingIds.update { it - id }
    }
  }
}
