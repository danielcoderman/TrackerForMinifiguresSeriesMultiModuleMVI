package com.drsapps.trackerforlegominifiguresseries.feature.minifigures.hide_minifigures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureHiddenState
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName
import com.drsapps.trackerforlegominifiguresseries.core.ui.components.BasicTopAppBar
import com.drsapps.trackerforlegominifiguresseries.feature.minifigures.hide_minifigures.components.MinifigureCheckbox
import com.drsapps.trackerforlegominifiguresseries.feature.minifigures.hide_minifigures.components.SeriesNameCard
import com.drsapps.trackerforlegominifiguresseries.core.ui.theme.TrackerForLegoMinifiguresSeriesTheme

@Composable
fun HideMinifiguresScreen(
    title: String,
    viewModel: HideMinifiguresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val series by viewModel.series.collectAsStateWithLifecycle()
    val selectedSeries = viewModel.selectedSeries
    val minifiguresFromSelectedSeries by viewModel.minifiguresFromSelectedSeries.collectAsStateWithLifecycle()

    HideMinifiguresScreen(
        title = title,
        series = series,
        selectedSeries = selectedSeries,
        onSeriesClick = viewModel::changeSelectedSeriesAndMinifigures,
        minifiguresFromSelectedSeries = minifiguresFromSelectedSeries,
        onMinifigureClick = viewModel::toggleMinifigureHiddenState,
        onNavigateBack = onNavigateBack,
        onDialogDismiss = viewModel::deselectSeries,
        onDialogConfirm = viewModel::deselectSeries
    )

}

@Composable
fun HideMinifiguresScreen(
    title: String,
    series: List<SeriesIdAndName>,
    selectedSeries: SeriesIdAndName,
    onSeriesClick: (id: Int, name: String) -> Unit,
    minifiguresFromSelectedSeries: List<MinifigureHiddenState>,
    onMinifigureClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onDialogDismiss: () -> Unit,
    onDialogConfirm: () -> Unit
) {

    val openHideMinifiguresDialog = selectedSeries.id != -1

    if (openHideMinifiguresDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            confirmButton = {
                TextButton(onClick = onDialogConfirm) {
                    Text("OK")
                }
            },
            title = {
                Text(selectedSeries.name)
            },
            text = {
                Column {
                    HorizontalDivider()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                    ) {
                        items(minifiguresFromSelectedSeries) { minifigure ->
                            MinifigureCheckbox(
                                minifigure = minifigure,
                                onClick = onMinifigureClick
                            )
                        }
                        // Non-urgent: I prefer having the HorizontalDivider appear when scrolling
                        // to the end of a list, like below, but having multiple item/items blocks
                        // appears to cause a bug where on process recreation (or whenever the
                        // data/list becomes empty for a bit) the scroll state of the LazyColumn
                        // is lost.
                        // https://issuetracker.google.com/issues/179397301
//                        item {
//                            HorizontalDivider()
//                        }
                    }
                    HorizontalDivider()
                }
            }
        )
    }

    Scaffold(
        topBar = {
            BasicTopAppBar(
                title = title,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->

        LazyVerticalGrid(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            columns = GridCells.Adaptive(330.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(series) { series ->
                SeriesNameCard(
                    series = series,
                    onSeriesClick = onSeriesClick
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun HideMinifiguresScreenPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        HideMinifiguresScreen(
            title = "Hide Minifigures",
            series = listOf(
                SeriesIdAndName(1, "Series 1"),
                SeriesIdAndName(2, "Series 2"),
                SeriesIdAndName(3, "Series 3")
            ),
            selectedSeries = SeriesIdAndName(2, "Series 2"),
            onSeriesClick = { _,_ -> },
            minifiguresFromSelectedSeries = listOf(
                MinifigureHiddenState(1, "Minifig 1", false),
                MinifigureHiddenState(2, "Minifig 2", true),
                MinifigureHiddenState(3, "Minifig 3", true),
                MinifigureHiddenState(4, "Minifig 4", false),
                MinifigureHiddenState(5, "Minifig 5", false),
                MinifigureHiddenState(6, "Minifig 6", true),
                MinifigureHiddenState(7, "Minifig 7", false),
                MinifigureHiddenState(8, "Minifig 8", false)
            ),
            onMinifigureClick = {},
            onNavigateBack = {},
            onDialogDismiss = {},
            onDialogConfirm = {}
        )
    }
}