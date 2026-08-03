package com.ryccoatika.sqatoolkit.common.utils

import com.ryccoatika.sqatoolkit.common.InvokeError
import com.ryccoatika.sqatoolkit.common.InvokeStarted
import com.ryccoatika.sqatoolkit.common.InvokeStatus
import com.ryccoatika.sqatoolkit.common.InvokeSuccess
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObservableLoadingCounter {
  private val count = AtomicInteger()
  private val loadingState = MutableStateFlow(count.get())

  val observable: Flow<Boolean>
    get() = loadingState.map { it > 0 }.distinctUntilChanged()

  fun addLoader() {
    loadingState.value = count.incrementAndGet()
  }

  fun removeLoader() {
    loadingState.value = count.decrementAndGet()
  }
}

suspend fun Flow<InvokeStatus>.collectStatus(
  counter: ObservableLoadingCounter,
) = collect { status ->
  when (status) {
    InvokeStarted -> counter.addLoader()

    InvokeSuccess -> {
      counter.removeLoader()
    }

    is InvokeError -> {
      counter.removeLoader()
    }
  }
}
