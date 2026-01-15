package com.drsapps.trackerforlegominifiguresseries.presentation.series_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.presentation.series_details.components.MinifigureCard
import com.drsapps.trackerforlegominifiguresseries.presentation.series_details.components.SeriesDetailsTopAppBar
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Layout
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun SeriesDetailsScreen(
    viewModel: SeriesDetailsViewModel = hiltViewModel(),
    title: String,
    onMinifigureClick: (id: Int, title: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isSeriesFavorite by viewModel.isSeriesFavorite.collectAsStateWithLifecycle()
    val layout by viewModel.layout.collectAsStateWithLifecycle()
    val minifigures by viewModel.minifigures.collectAsStateWithLifecycle()

    SeriesDetailsScreen(
        title = title,
        isSeriesFavorite = isSeriesFavorite,
        onToggleSeriesFavoriteState = viewModel::toggleSeriesFavoriteState,
        layout = layout,
        onToggleLayout = viewModel::toggleLayout,
        minifigures = minifigures,
        onMinifigureClick = onMinifigureClick,
        toggleMinifigureCollectedStateAndResetWishlistedState = viewModel::toggleMinifigureCollectedStateAndResetWishlistedState,
        toggleMinifigureWishlistedStateAndResetCollectedState = viewModel::toggleMinifigureWishlistedStateAndResetCollectedState,
        toggleMinifigureFavoriteState = viewModel::toggleMinifigureFavoriteState,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun SeriesDetailsScreen(
    title: String,
    isSeriesFavorite: Boolean?,
    onToggleSeriesFavoriteState: () -> Unit,
    layout: Layout?,
    onToggleLayout: () -> Unit,
    minifigures: List<Minifigure>,
    onMinifigureClick: (id: Int, title: String) -> Unit,
    toggleMinifigureCollectedStateAndResetWishlistedState: (Int) -> Unit,
    toggleMinifigureWishlistedStateAndResetCollectedState: (Int) -> Unit,
    toggleMinifigureFavoriteState: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {

    Scaffold(
        topBar = {
            SeriesDetailsTopAppBar(
                title = title,
                isSeriesFavorite = isSeriesFavorite,
                onToggleSeriesFavoriteState = onToggleSeriesFavoriteState,
                layout = layout,
                onToggleLayout = onToggleLayout,
                onNavigateBack =  onNavigateBack
            )
        }
    ) { paddingValues ->
        if (layout == Layout.GRID) {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .fillMaxSize(),
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(minifigures) { minifigure ->
                    MinifigureCard(
                        minifigure = minifigure,
                        onClick = onMinifigureClick,
                        toggleMinifigureCollectedStateAndResetWishlistedState = toggleMinifigureCollectedStateAndResetWishlistedState,
                        toggleMinifigureWishlistedStateAndResetCollectedState = toggleMinifigureWishlistedStateAndResetCollectedState,
                        toggleMinifigureFavoriteState = toggleMinifigureFavoriteState
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(minifigures) { minifigure ->
                    MinifigureCard(
                        minifigure = minifigure,
                        onClick = onMinifigureClick,
                        toggleMinifigureCollectedStateAndResetWishlistedState = toggleMinifigureCollectedStateAndResetWishlistedState,
                        toggleMinifigureWishlistedStateAndResetCollectedState = toggleMinifigureWishlistedStateAndResetCollectedState,
                        toggleMinifigureFavoriteState = toggleMinifigureFavoriteState
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun SeriesDetailsScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesDetailsScreen(
            title = "Series 1",
            isSeriesFavorite = true,
            onToggleSeriesFavoriteState = {},
            layout = Layout.GRID,
            onToggleLayout = {},
            minifigures = listOf(
                Minifigure(
                    id = 1,
                    name = "Minifig 1",
                    imageUrl = "",
                    positionInSeries = 1,
                    seriesId = 1,
                    collected = true,
                    wishListed = false,
                    favorite = true,
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
                    collected = false,
                    wishListed = false,
                    favorite = false,
                    hidden = false,
                    numOfCollectedInventoryItems = 0,
                    inventorySize = 5
                ),
                Minifigure(
                    id = 3,
                    name = "Minifig 3",
                    imageUrl = "",
                    positionInSeries = 3,
                    seriesId = 1,
                    collected = false,
                    wishListed = true,
                    favorite = true,
                    hidden = false,
                    numOfCollectedInventoryItems = 4,
                    inventorySize = 5
                ),
                Minifigure(
                    id = 4,
                    name = "Minifig 4",
                    imageUrl = "",
                    positionInSeries = 4,
                    seriesId = 1,
                    collected = false,
                    wishListed = false,
                    favorite = true,
                    hidden = false,
                    numOfCollectedInventoryItems = 3,
                    inventorySize = 5
                )
            ),
            onMinifigureClick = { _,_ -> },
            toggleMinifigureCollectedStateAndResetWishlistedState = {},
            toggleMinifigureWishlistedStateAndResetCollectedState = {},
            toggleMinifigureFavoriteState = {},
            onNavigateBack = {}
        )
    }
}