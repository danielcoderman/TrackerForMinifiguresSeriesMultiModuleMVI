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
import androidx.compose.runtime.DisposableEffect
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
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val series by viewModel.series.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isDataRefreshing.collectAsStateWithLifecycle()
    val refreshMessage by viewModel.refreshMessage.collectAsStateWithLifecycle()

    SeriesScreen(
        series = series,
        filter = filter,
        changeFilterAndSeries = viewModel::changeFilterAndSeries,
        onSeriesImageClick = onSeriesImageClick,
        onSeriesClick = onSeriesClick,
        onSearchClicked = onSearchClicked,
        onVisibilityClicked = onVisibilityClicked,
        isNavigationRailShown = isNavigationRailShown,
        isRefreshing = isRefreshing,
        refreshData = viewModel::refreshData,
        refreshMessage = refreshMessage,
        clearRefreshMessage = viewModel::clearRefreshMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    series: List<Series>,
    filter: SeriesFilter,
    changeFilterAndSeries: (SeriesFilter) -> Unit,
    onSeriesImageClick: (id: Int) -> Unit,
    onSeriesClick: (id: Int, title: String) -> Unit,
    onSearchClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    isNavigationRailShown: Boolean,
    isRefreshing: Boolean,
    refreshData: () -> Unit,
    refreshMessage: String,
    clearRefreshMessage: () -> Unit
) {
    DisposableEffect(Unit) {
        // Code can be placed here to run when DisposableEffect enters the composition

        // This runs when the DisposableEffect composable leaves the composition
        onDispose {
            // Clear the refresh message when DisposableEffect leaves the composition which is when
            // the SeriesScreen composable leaves the composition. Why do this? Because if the user
            // reads the refresh message through the snackbar and leaves to another screen before
            // the snackbar disappears, then when returning to the Series screen they will see a
            // snackbar with the same refresh message again (The ViewModel didn't get destroyed),
            // and we don't want that.
            clearRefreshMessage()
        }
    }

    // Used for collapsing or hiding the top app bar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Used to show a snackbar
    val snackbarHostState = remember { SnackbarHostState() }

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
        LaunchedEffect(refreshMessage) {
            if (refreshMessage.isNotEmpty()) {
                snackbarHostState.showSnackbar(refreshMessage)
                // Clear the refresh message once the snackbar has disappeared
                clearRefreshMessage()
            }
        }

        // to reset scroll
        val lazyGridState = rememberLazyGridState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            SeriesFiltersScrollableTabRow(
                filter = filter,
                resetScrollingPosition = {
                    lazyGridState.scrollToItem(0)
                },
                changeFilterAndSeries = changeFilterAndSeries,
                isNavigationRailShown = isNavigationRailShown
            )

            if (series.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 16.dp)
                ) {
                    when (filter) {
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
                    isRefreshing = isRefreshing,
                    onRefresh = refreshData,
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(315.dp),
                        state = lazyGridState,
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(series) { series ->
                            SeriesCard(
                                series = series,
                                onSeriesImageClick = onSeriesImageClick,
                                onSeriesClick = onSeriesClick,
                                filter = filter
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
            changeFilterAndSeries = {},
            onSeriesImageClick = {},
            onSeriesClick = { _,_ -> },
            onSearchClicked = {},
            onVisibilityClicked = {},
            isNavigationRailShown = false,
            isRefreshing = false,
            refreshData = {},
            refreshMessage = "",
            clearRefreshMessage = {}
        )
    }
}