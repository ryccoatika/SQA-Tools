package com.ryccoatika.sqatoolkit.devinfo.ui.main

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.ItemComposer
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalPermissionRequester
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalSnackbarHostState
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Main = @Composable (
  navigateUp: () -> Unit,
  dynamicColor: Boolean,
  onToggleDynamicColor: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun Main(
  types: Set<DevInfoType>,
  viewModelFactory: () -> DevInfoViewModel,
  reportFormatter: DevInfoReportFormatter,
  @Assisted navigateUp: () -> Unit,
  @Assisted dynamicColor: Boolean,
  @Assisted onToggleDynamicColor: () -> Unit,
) {
  val vm = viewModel(factory = viewModelFactory)
  val state by vm.state.collectAsState()
  val context = LocalContext.current
  val textCreator = LocalTextCreator.current
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()
  val orderedTypes = remember(types) { types.sortedBy { it.order } }
  val pagerState = rememberPagerState { orderedTypes.size }
  var searchActive by remember { mutableStateOf(false) }

  val currentType = orderedTypes.getOrNull(pagerState.currentPage)
  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { granted -> if (granted) currentType?.let { vm.refresh(it) } }

  CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
    Scaffold(
      topBar = {
        MainTopBar(
          dynamicColor = dynamicColor,
          onToggleSearch = {
            searchActive = !searchActive
            if (!searchActive) vm.onQueryChange("")
          },
          onShare = {
            val report = reportFormatter.format(state.tabs)
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, report)
            }
            context.startActivity(Intent.createChooser(intent, null))
          },
          onToggleDynamicColor = onToggleDynamicColor,
          navigateUp = navigateUp,
        )
      },
      snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
      Column(
        modifier = Modifier.padding(paddingValues),
      ) {
        AnimatedVisibility(visible = searchActive) {
          OutlinedTextField(
            value = state.query,
            onValueChange = vm::onQueryChange,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.di_action_search)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            singleLine = true,
          )
        }

        if (state.query.isBlank()) {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
          ) {
            items(
              items = orderedTypes,
              key = { it.id },
            ) { type ->
              FilterChip(
                selected = type == currentType,
                onClick = {
                  coroutineScope.launch {
                    pagerState.animateScrollToPage(orderedTypes.indexOf(type))
                  }
                },
                label = { Text(textCreator.deviceInfoTypeTitle(type)) },
                leadingIcon = {
                  Icon(
                    imageVector = tabIcon(type.id),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                },
              )
            }
          }
          CompositionLocalProvider(
            LocalPermissionRequester provides { permissionLauncher.launch(it) },
          ) {
            HorizontalPager(
              state = pagerState,
              modifier = Modifier.weight(1f),
              key = { orderedTypes[it].id },
            ) { page ->
              val type = orderedTypes[page]
              val items = state.tabs.firstOrNull { it.type.id == type.id }?.items.orEmpty()
              TabContent(items = items)
            }
          }
        } else {
          CompositionLocalProvider(
            LocalPermissionRequester provides { permissionLauncher.launch(it) },
          ) {
            SearchResults(
              tabs = state.tabs,
              query = state.query,
              textCreator = textCreator,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TabContent(
  items: List<Item>,
) {
  ItemComposer(
    items = items,
    modifier = Modifier
      .padding(vertical = 8.dp, horizontal = 16.dp)
      .verticalScroll(state = rememberScrollState())
      .fillMaxSize(),
  )
}

@Composable
private fun SearchResults(
  tabs: List<TabData>,
  query: String,
  textCreator: DevInfoTextCreator,
) {
  val grouped = tabs.mapNotNull { tab ->
    val filtered = filterItems(tab.items, query)
    if (filtered.isEmpty()) null else tab to filtered
  }

  if (grouped.isEmpty()) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stringResource(R.string.di_search_no_matches),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    return
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    grouped.forEach { (tab, filtered) ->
      item(key = "header_${tab.type.id}") {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = tabIcon(tab.type.id),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
          )
          Text(
            text = textCreator.deviceInfoTypeTitle(tab.type),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
          )
          Text(
            text = "${filtered.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      item(key = "items_${tab.type.id}") {
        ItemComposer(items = filtered)
      }
    }
  }
}

@Composable
private fun MainTopBar(
  dynamicColor: Boolean,
  onToggleSearch: () -> Unit,
  onShare: () -> Unit,
  onToggleDynamicColor: () -> Unit,
  navigateUp: () -> Unit,
) {
  var menuExpanded by remember { mutableStateOf(false) }

  AppTopBar(
    title = stringResource(R.string.di_title),
    onBackPressed = navigateUp,
    actions = {
      IconButton(onClick = onToggleSearch) {
        Icon(
          imageVector = Icons.Rounded.Search,
          contentDescription = stringResource(R.string.di_action_search),
        )
      }
      IconButton(onClick = onShare) {
        Icon(
          imageVector = Icons.Rounded.Share,
          contentDescription = stringResource(R.string.di_action_share),
        )
      }
      IconButton(onClick = { menuExpanded = true }) {
        Icon(
          imageVector = Icons.Rounded.MoreVert,
          contentDescription = stringResource(R.string.di_action_more),
        )
      }
      DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false },
      ) {
        DropdownMenuItem(
          text = { Text(stringResource(R.string.di_action_dynamic_color)) },
          onClick = {
            onToggleDynamicColor()
            menuExpanded = false
          },
          trailingIcon = {
            if (dynamicColor) {
              Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
              )
            }
          },
        )
      }
    },
  )
}
