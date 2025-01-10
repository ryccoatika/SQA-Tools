package com.ryccoatika.sqatools.fillstorage.ui.manage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.fillstorage.core.usecase.GetStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
internal class ManageViewModel(
  @Assisted savedStateHandle: SavedStateHandle,
  getStorage: GetStorage,
) : ViewModel() {
  private val path: String = savedStateHandle["path"] ?: ""

  val state: StateFlow<ManageViewState> = getStorage(GetStorage.Params(path))
    .map { storage ->
      ManageViewState(
        storage = storage,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = ManageViewState.Empty,
    )
}
