package com.drsapps.trackerforlegominifiguresseries.presentation.hide_series

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesHiddenState
import com.drsapps.trackerforlegominifiguresseries.presentation.components.BasicTopAppBar
import com.drsapps.trackerforlegominifiguresseries.presentation.hide_series.components.SeriesCheckbox
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Screen
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun HideSeriesScreen(
    viewModel: HideSeriesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

    val series by viewModel.series.collectAsStateWithLifecycle()

    HideSeriesScreen(
        series = series,
        onHideSeries = viewModel::hideSeries,
        onUnhideSeries = viewModel::unhideSeries,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun HideSeriesScreen(
    series: List<SeriesHiddenState>,
    onHideSeries: (Int) -> Unit,
    onUnhideSeries: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {

    Scaffold (
        topBar = {
            BasicTopAppBar(
                title = Screen.HideSeries.title!!,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            columns = GridCells.Adaptive(330.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(series) { series ->
                SeriesCheckbox(
                    series = series,
                    onHideSeries = onHideSeries,
                    onUnhideSeries = onUnhideSeries
                )
            }
        }
    }

}

@Preview
@Composable
fun HideSeriesScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        HideSeriesScreen(
            series = listOf(
                SeriesHiddenState(id = 1, name = "Series 1", 12, 12),
                SeriesHiddenState(id = 2, name = "Series 2", 12, 10),
                SeriesHiddenState(id = 3, name = "Series 3", 12, 0),
            ),
            onHideSeries = {},
            onUnhideSeries = {},
            onNavigateBack = {}
        )
    }
}