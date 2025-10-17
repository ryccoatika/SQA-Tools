@file:Suppress("UNCHECKED_CAST")

package com.ryccoatika.sqatools.common.extensions

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

@Composable
inline fun <reified VM : ViewModel> viewModel(
  viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
    "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
  },
  key: String? = null,
  extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
    viewModelStoreOwner.defaultViewModelCreationExtras
  } else {
    CreationExtras.Empty
  },
  crossinline factory: () -> VM,
): VM = androidx.lifecycle.viewmodel.compose.viewModel(
  viewModelStoreOwner = viewModelStoreOwner,
  key = key,
  extras = extras,
  factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = factory() as T
  },
)

@Composable
inline fun <reified VM : ViewModel> viewModel(
  viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
    "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
  },
  key: String? = null,
  crossinline factory: (SavedStateHandle) -> VM,
): VM = androidx.lifecycle.viewmodel.compose.viewModel(
  viewModelStoreOwner = viewModelStoreOwner,
  key = key,
  factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
      modelClass: Class<T>,
      extras: CreationExtras,
    ): T = factory(extras.createSavedStateHandle()) as T
  },
)
