package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drsapps.trackerforlegominifiguresseries.R
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.presentation.components.SeriesAndMinifiguresTopAppBar
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigures.components.MinifigureImageCard
import com.drsapps.trackerforlegominifiguresseries.presentation.minifigures.components.MinifiguresFiltersScrollableTabRow
import com.drsapps.trackerforlegominifiguresseries.presentation.util.MinifiguresFilter
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Screen
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
// TODO("Add snackbar usage here. Use the SeriesScreen as a reference")
@Composable
fun MinifiguresScreen(
    viewModel: MinifiguresViewModel = hiltViewModel(),
    onMinifigureClick: (id: Int, title: String) -> Unit,
    onSearchClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    isNavigationRailShown: Boolean
) {

    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val minifigures by viewModel.minifigures.collectAsStateWithLifecycle()

    MinifiguresScreen(
        filter = filter,
        minifigures = minifigures,
        changeFilterAndMinifigures = viewModel::changeFilterAndMinifigures,
        onMinifigureClick = onMinifigureClick,
        onSearchClicked = onSearchClicked,
        onVisibilityClicked = onVisibilityClicked,
        isNavigationRailShown = isNavigationRailShown
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinifiguresScreen(
    filter: MinifiguresFilter,
    minifigures: List<Minifigure>,
    changeFilterAndMinifigures: (MinifiguresFilter) -> Unit,
    onMinifigureClick: (id: Int, title: String) -> Unit,
    onSearchClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    isNavigationRailShown: Boolean
) {

    // Used for collapsing or hiding the top app bar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SeriesAndMinifiguresTopAppBar(
                title = Screen.Minifigures.title!!,
                onSearchClicked = onSearchClicked,
                onVisibilityClicked = onVisibilityClicked,
                searchIconDescResId = R.string.search_minifigures,
                visibilityIconDescResId = R.string.hide_minifigures,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            // to reset scroll
            val lazyGridState = rememberLazyGridState()

            MinifiguresFiltersScrollableTabRow(
                filter = filter,
                resetScrollingPosition = {
                    lazyGridState.scrollToItem(0)
                },
                changeFilterAndMinifigures = changeFilterAndMinifigures,
                isNavigationRailShown = isNavigationRailShown
            )

            if (minifigures.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 16.dp)
                ) {
                    when (filter) {
                        // Note: Don't have to worry about the case where the "All" filter displays
                        // no minifigures. That would really only happen if someone purposely hides
                        // all the series. Showing a blank screen is fine in that case.
                        MinifiguresFilter.ALL -> Unit
                        MinifiguresFilter.FAVORITES -> Text("No minifigures have been favorited")
                        MinifiguresFilter.COLLECTED -> Text("No minifigures have been collected")
                        MinifiguresFilter.WISHLIST -> Text("No minifigures have been wishlisted")
                        MinifiguresFilter.UNCOLLECTED -> Text("No minifigures left to collect!") // Non-urgent: Maybe don't allow all the minifigures to be hidden
                    }
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(100.dp),
                    state = lazyGridState,
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(minifigures) { minifigure ->
                        MinifigureImageCard(
                            minifigure = minifigure,
                            onClick = onMinifigureClick,
                            filter = filter
                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun MinifiguresScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifiguresScreen(
            filter = MinifiguresFilter.ALL,
            minifigures = listOf(
                Minifigure(
                    id = 1,
                    name = "Minifig 1",
                    imageUrl = "",
                    positionInSeries = 1,
                    seriesId = 1,
                    favorite = false,
                    collected = true,
                    wishListed = false,
                    hidden = false,
                    numOfCollectedInventoryItems = 5,
                    inventorySize = 5
                ),
                Minifigure(
                    id = 2,
                    name = "Minifig 2",
                    imageUrl = "",
                    positionInSeries = 2,
                    seriesId = 1,
                    favorite = false,
                    collected = false,
                    wishListed = false,
                    hidden = false,
                    numOfCollectedInventoryItems = 2,
                    inventorySize = 5,
                ),
                Minifigure(
                    id = 3,
                    name = "Minifig 3",
                    imageUrl = "",
                    positionInSeries = 3,
                    seriesId = 1,
                    favorite = false,
                    collected = false,
                    wishListed = false,
                    hidden = false,
                    numOfCollectedInventoryItems = 0,
                    inventorySize = 5
                ),
                Minifigure(
                    id = 4,
                    name = "Minifig 4",
                    imageUrl = "",
                    positionInSeries = 4,
                    seriesId = 1,
                    favorite = false,
                    collected = true,
                    wishListed = false,
                    hidden = false,
                    numOfCollectedInventoryItems = 5,
                    inventorySize = 5
                )
            ),
            changeFilterAndMinifigures = {},
            onMinifigureClick = {_,_ ->},
            onSearchClicked = {},
            onVisibilityClicked = {},
            isNavigationRailShown = false
        )
    }
}