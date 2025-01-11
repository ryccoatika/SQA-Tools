package com.ryccoatika.sqatools.common

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

sealed class InvokeStatus
data object InvokeStarted : InvokeStatus()
data object InvokeSuccess : InvokeStatus()
data class InvokeError(val throwable: Throwable) : InvokeStatus()

abstract class Interactor<in P> {
  operator fun invoke(
    params: P,
    timeoutMs: Long = defaultTimeoutMs,
  ): Flow<InvokeStatus> = flow {
    try {
      withTimeout(timeoutMs) {
        emit(InvokeStarted)
        doWork(params)
        emit(InvokeSuccess)
      }
    } catch (t: TimeoutCancellationException) {
      emit(InvokeError(t))
    }
  }.catch { t -> emit(InvokeError(t)) }

  protected abstract suspend fun doWork(params: P)

  companion object {
    private val defaultTimeoutMs = TimeUnit.MINUTES.toMillis(5)
  }
}

abstract class ResultInteractor<in P, R> {
  operator fun invoke(params: P): Flow<R> = flow {
    emit(doWork(params))
  }

  protected abstract suspend fun doWork(params: P): R
}

abstract class ProgressInteractor<P : Any, T> {
  private var job: Job? = null

  private val _progress = MutableSharedFlow<T?>(
    replay = 1,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )
  val progress: Flow<T?> = _progress
    .distinctUntilChanged()

  init {
    _progress.tryEmit(null)
  }

  operator fun invoke(params: P) {
    job = doWork(params, _progress::tryEmit)
  }

  fun cancel() {
    job?.cancel()
    job = null
    _progress.tryEmit(null)
  }

  protected abstract fun doWork(params: P, progressEmitter: (T) -> Unit): Job
}

abstract class SubjectInteractor<P : Any, T> {
  private val paramState = MutableSharedFlow<P>(
    replay = 1,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )

  val flow: Flow<T> = paramState
    .distinctUntilChanged()
    .flatMapLatest { createObservable(it) }
    .distinctUntilChanged()

  operator fun invoke(params: P) {
    paramState.tryEmit(params)
  }

  protected abstract fun createObservable(params: P): Flow<T>
}
