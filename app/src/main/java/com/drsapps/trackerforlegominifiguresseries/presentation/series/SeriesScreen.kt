package com.drsapps.trackerforlegominifiguresseries.presentation.series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.presentation.components.SeriesAndMinifiguresTopAppBar
import com.drsapps.trackerforlegominifiguresseries.presentation.components.SeriesCard
import com.drsapps.trackerforlegominifiguresseries.presentation.series.components.SeriesFiltersScrollableTabRow
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Screen
import com.drsapps.trackerforlegominifiguresseries.presentation.util.SeriesFilter
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun SeriesScreen(
    viewModel: SeriesViewModel = hiltViewModel(),
    onSeriesImageClick: (id: Int) -> Unit,
    onSeriesClick: (id: Int, title: String) -> Unit,
    onSearchClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    isNavigationRailShown: Boolean
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Hoisted here, rather than inside the stateless SeriesScreen below, so this effect
    // collector and the Scaffold that displays the snackbar share the same SnackbarHostState.
    // A Channel-backed effect is delivered once to whichever collector is attached when it's
    // sent, so unlike a value sitting in State there's no stale value left behind to clear when
    // the screen leaves composition.
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SeriesEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    SeriesScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onSeriesImageClick = onSeriesImageClick,
        onSeriesClick = onSeriesClick,
        onSearchClicked = onSearchClicked,
        onVisibilityClicked = onVisibilityClicked,
        isNavigationRailShown = isNavigationRailShown,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    uiState: SeriesUiState,
    onEvent: (SeriesEvent) -> Unit,
    onSeriesImageClick: (id: Int) -> Unit,
    onSeriesClick: (id: Int, title: String) -> Unit,
    onSearchClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    isNavigationRailShown: Boolean,
    snackbarHostState: SnackbarHostState
) {
    // Used for collapsing or hiding the top app bar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SeriesAndMinifiguresTopAppBar(
                title = Screen.Series.title!!,
                onSearchClicked = onSearchClicked,
                onVisibilityClicked = onVisibilityClicked,
                searchIconDescResId = R.string.search_series,
                visibilityIconDescResId = R.string.hide_series,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        // to reset scroll
        val lazyGridState = rememberLazyGridState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            SeriesFiltersScrollableTabRow(
                filter = uiState.filter,
                resetScrollingPosition = {
                    lazyGridState.scrollToItem(0)
                },
                changeFilterAndSeries = { filter -> onEvent(SeriesEvent.FilterChanged(filter)) },
                isNavigationRailShown = isNavigationRailShown
            )

            if (uiState.series.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 16.dp)
                ) {
                    when (uiState.filter) {
                        // Note: Don't have to worry about the case where the "All" filter displays
                        // no series. That would really only happen if someone purposely hides
                        // all the series. Showing a blank screen is fine in that case.
                        SeriesFilter.ALL -> Unit
                        SeriesFilter.FAVORITES -> Text("No series have been favorited")
                        SeriesFilter.COMPLETED -> Text("No series have been completed")
                        SeriesFilter.UNCOMPLETED -> Text("No series left to complete!") // Non-urgent: Maybe don't allow all the series to be hidden
                    }
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { onEvent(SeriesEvent.Refresh) },
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(315.dp),
                        state = lazyGridState,
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.series) { series ->
                            SeriesCard(
                                series = series,
                                onSeriesImageClick = onSeriesImageClick,
                                onSeriesClick = onSeriesClick,
                                filter = uiState.filter
                            )
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun SeriesScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesScreen(
            uiState = SeriesUiState(
                series = listOf(
                    Series(
                        id = 1,
                        name = "Series 1",
                        imageUrl = "",
                        numOfMinifigs = 16,
                        releaseDate = "2010-06-01",
                        favorite = true,
                        numOfMinifigsCollected = 12,
                        numOfMinifigsHidden = 2
                    ),
                    Series(
                        id = 2,
                        name = "Series 2",
                        imageUrl = "",
                        numOfMinifigs = 16,
                        releaseDate = "2010-09-01",
                        favorite = true,
                        numOfMinifigsCollected = 13,
                        numOfMinifigsHidden = 0
                    )
                ),
                filter = SeriesFilter.FAVORITES,
                isRefreshing = false
            ),
            onEvent = {},
            onSeriesImageClick = {},
            onSeriesClick = { _,_ -> },
            onSearchClicked = {},
            onVisibilityClicked = {},
            isNavigationRailShown = false,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}