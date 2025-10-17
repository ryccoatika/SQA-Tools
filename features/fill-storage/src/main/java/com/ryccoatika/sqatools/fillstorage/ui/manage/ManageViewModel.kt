package com.ryccoatika.sqatools.fillstorage.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.fillstorage.core.model.FillStorage as FillStorageParam
import com.ryccoatika.sqatools.fillstorage.core.usecase.FillStorage
import com.ryccoatika.sqatools.fillstorage.core.usecase.ObserveStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
internal class ManageViewModel(
  @Assisted savedStateHandle: SavedStateHandle,
  observeStorage: ObserveStorage,
  private val fillStorage: FillStorage,
) : ViewModel() {
  val path: String = savedStateHandle.get<String?>("path").orEmpty()

  val state: StateFlow<ManageViewState> = combine(
    observeStorage.flow,
    fillStorage.progress,
    ::ManageViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = ManageViewState.Empty,
  )

  init {
    observeStorage(ObserveStorage.Params(path))
  }

  fun fillStorage(fillStorage: FillStorageParam) {
    fillStorage(FillStorage.Params(state.value.storage, fillStorage))
  }

  fun dismissProgress() {
    fillStorage.cancel()
  }
}
