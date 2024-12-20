package com.ryccoatika.sqatools.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatools.common.extensions.viewModel
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import me.tatarka.inject.annotations.Inject

internal typealias Home = @Composable () -> Unit

@Inject
@Composable
internal fun Home(
  viewModelFactory: () -> HomeViewModel,
) {
  Home(
    viewModel = viewModel(factory = viewModelFactory),
  )
}

@Composable
private fun Home(
  viewModel: HomeViewModel,
) {
  val viewState by viewModel.state.collectAsState()

  Home(
    state = viewState,
  )
}

@Composable
private fun Home(
  state: HomeViewState,
) {
  val context = LocalContext.current
  Scaffold { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .padding(paddingValues),
    ) {
      items(state.features.toList()) { feature ->
        ListItem(
          leadingContent = {
            Icon(
              imageVector = feature.icon,
              contentDescription = null,
            )
          },
          headlineContent = {
            Text(
              text = stringResource(id = feature.featureTitle),
            )
          },
          supportingContent = {
            Text(
              text = stringResource(id = feature.featureDescription),
            )
          },
          modifier = Modifier
            .clickable {
              feature.open(context)
            },
        )
      }
    }
  }
}

@Preview
@Composable
private fun HomePreview() {
  SQAToolsTheme {
    Home(
      state = HomeViewState.Empty,
    )
  }
}
