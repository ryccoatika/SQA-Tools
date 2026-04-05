package com.ryccoatika.sqatoolkit.fillmemory.ui.home.floater

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ryccoatika.sqatoolkit.common.ui.VerticalSpace
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillmemory.R
import com.ryccoatika.sqatoolkit.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatoolkit.fillmemory.core.utils.FillMemoryTextCreator
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.preview.CompositionLocalProviderForPreview

internal class FloaterFillMemory(
  context: Context,
  private val viewModel: FloaterFillMemoryViewModel,
  private val lifecycleOwner: LifecycleOwner?,
  private val savedStateRegistryOwner: SavedStateRegistryOwner?,
  private val openApp: () -> Unit,
) {
  private var onDismissRequest: () -> Unit = {}

  private val view: View by lazy {
    object : FrameLayout(context) {
      override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
          onDismissRequest()
        }
        return super.dispatchKeyEvent(event)
      }
    }.apply {
      setViewTreeLifecycleOwner(lifecycleOwner)
      setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)

      addView(
        ComposeView(context).apply {
          setViewTreeLifecycleOwner(lifecycleOwner)
          setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
          setContent {
            val state by viewModel.state.collectAsState()

            CompositionLocalProvider(
              LocalTextCreator provides FillMemoryTextCreator(context),
            ) {
              FloaterFillMemoryContent(
                state = state,
                onFillMemory = viewModel::fillMemory,
                onClearMemory = viewModel::clearMemory,
                openApp = openApp,
                onDismissRequest = onDismissRequest,
              )
            }
          }
        },
        FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        ),
      )
    }
  }

  private val layoutParams by lazy {
    LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LayoutParams.TYPE_APPLICATION_OVERLAY
      } else {
        @Suppress("DEPRECATION")
        LayoutParams.TYPE_PHONE
      },
      LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT,
    )
  }

  fun show(windowManager: WindowManager) {
    windowManager.addView(view, layoutParams)
    onDismissRequest = {
      close(windowManager)
    }
  }

  private fun close(windowManager: WindowManager) {
    windowManager.removeView(view)
  }
}

@Composable
private fun FloaterFillMemoryContent(
  state: FloaterFillMemoryViewState,
  onFillMemory: (FillMemory) -> Unit,
  onClearMemory: () -> Unit,
  openApp: () -> Unit,
  onDismissRequest: () -> Unit,
) {
  val textCreator = LocalTextCreator.current

  SQAToolsTheme {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .fillMaxSize()
        .clickable(
          enabled = true,
          interactionSource = null,
          indication = null,
          onClick = onDismissRequest,
        ),
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 450.dp)
          .padding(horizontal = 16.dp),
      ) {
        Surface(
          shape = MaterialTheme.shapes.medium,
          shadowElevation = 16.dp,
          modifier = Modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = {},
          ),
        ) {
          Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
          ) {
            Text(
              text = textCreator.memoryTotalText(state.memoryUsage),
            )
            Text(
              text = textCreator.memoryFreeText(state.memoryUsage),
            )
          }
        }
        if (state.isLoading) {
          8.VerticalSpace()
          if (state.fillMemoryProgress != null) {
            LinearProgressIndicator(
              progress = { state.fillMemoryProgress.progress },
              modifier = Modifier.fillMaxWidth(),
            )
          } else {
            LinearProgressIndicator(
              modifier = Modifier.fillMaxWidth(),
            )
          }
        }
        8.VerticalSpace()
        Surface(
          shape = MaterialTheme.shapes.medium,
          shadowElevation = 16.dp,
          modifier = Modifier.clickable(
            interactionSource = null,
            indication = null,
            onClick = {},
          ),
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = stringResource(R.string.fm_title),
                modifier = Modifier
                  .padding(horizontal = 16.dp)
                  .weight(1f),
              )
              IconButton(
                onClick = openApp,
              ) {
                Icon(
                  imageVector = Icons.Outlined.Home,
                  contentDescription = null,
                )
              }
              IconButton(
                onClick = onDismissRequest,
              ) {
                Icon(
                  imageVector = Icons.Outlined.Close,
                  contentDescription = null,
                )
              }
            }
            Column(
              modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            ) {
              LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
              ) {
                items(
                  items = listOf(
                    FillMemory(100.0),
                    FillMemory(500.0),
                    FillMemory(1000.0),
                  ),
                ) { option ->
                  Button(
                    shape = MaterialTheme.shapes.medium,
                    enabled = !state.isLoading,
                    onClick = {
                      onFillMemory(option)
                    },
                  ) {
                    Text(
                      text = textCreator.fillMemoryButtonText(option),
                      fontSize = 12.sp,
                    )
                  }
                }
              }
              Button(
                onClick = onClearMemory,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = stringResource(R.string.fm_button_clear_memory),
                )
              }
              state.fillMemoryProgress?.error?.let { error ->
                8.VerticalSpace()
                Text(
                  text = textCreator.errorMessage(error),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.error,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth(),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun FloaterFillMemoryContentPreview() {
  CompositionLocalProviderForPreview {
    FloaterFillMemoryContent(
      state = FloaterFillMemoryViewState.Empty,
      onFillMemory = {},
      onClearMemory = {},
      openApp = {},
      onDismissRequest = {},
    )
  }
}
