package com.ryccoatika.sqatools.fillstorage.ui.dummyfiles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatools.common.utils.ObservableLoadingCounter
import com.ryccoatika.sqatools.common.utils.collectStatus
import com.ryccoatika.sqatools.fillstorage.core.model.DummyFile
import com.ryccoatika.sqatools.fillstorage.core.usecase.DeleteFile
import com.ryccoatika.sqatools.fillstorage.core.usecase.GetDummyFiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
internal class DummyFilesViewModel(
  @Assisted savedStateHandle: SavedStateHandle,
  private val getDummyFiles: GetDummyFiles,
  private val deleteFile: DeleteFile,
) : ViewModel() {
  private val path: String = savedStateHandle["path"] ?: ""
  private val dummyFiles = MutableStateFlow<List<DummyFile>>(emptyList())

  private val loadingState = ObservableLoadingCounter()

  val state: StateFlow<DummyFilesViewState> = combine(
    dummyFiles,
    loadingState.observable,
    ::DummyFilesViewState,
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = DummyFilesViewState.Empty,
  )

  init {
    fetchDummyFiles()
  }

  private fun fetchDummyFiles() {
    viewModelScope.launch {
      loadingState.addLoader()
      dummyFiles.value = getDummyFiles.executeSync(GetDummyFiles.Params(path))
      loadingState.removeLoader()
    }
  }

  fun deleteFile(dummyFile: DummyFile) {
    viewModelScope.launch {
      deleteFile(DeleteFile.Params.SingleFile(dummyFile))
        .collectStatus(loadingState)
    }.invokeOnCompletion {
      fetchDummyFiles()
    }
  }

  fun deleteAllFiles() {
    viewModelScope.launch {
      deleteFile(DeleteFile.Params.AllFiles(path))
        .collectStatus(loadingState)
    }.invokeOnCompletion {
      fetchDummyFiles()
    }
  }
}
