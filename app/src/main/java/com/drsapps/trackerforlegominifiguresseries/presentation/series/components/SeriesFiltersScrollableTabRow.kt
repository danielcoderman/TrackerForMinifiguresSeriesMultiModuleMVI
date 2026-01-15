package com.drsapps.trackerforlegominifiguresseries.presentation.series.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drsapps.trackerforlegominifiguresseries.presentation.util.SeriesFilter
import com.drsapps.trackerforlegominifiguresseries.presentation.util.seriesFilters
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesFiltersScrollableTabRow(
    filter: SeriesFilter,
    resetScrollingPosition: suspend () -> Unit,
    changeFilterAndSeries: (SeriesFilter) -> Unit,
    isNavigationRailShown: Boolean
) {

    // Creates a CoroutineScope bound to the SeriesFiltersScrollableTabRow's lifecycle
    val coroutineScope = rememberCoroutineScope()

    val selectedTabIndex = filter.ordinal

    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            divider = {}
        ) {
            seriesFilters.forEach { seriesFilter ->
                val isSelected = seriesFilter.ordinal == selectedTabIndex
                Tab(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            coroutineScope.launch {
                                resetScrollingPosition()
                            }
                            changeFilterAndSeries(seriesFilter)
                        }
                    },
                    text = {
                        Text(text = seriesFilter.displayName)
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!isNavigationRailShown) {
            HorizontalDivider()
        }
    }

}

@Preview
@Composable
fun SeriesFiltersScrollableTabRowPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        SeriesFiltersScrollableTabRow(
            filter = SeriesFilter.COMPLETED,
            resetScrollingPosition = {},
            changeFilterAndSeries = {},
            isNavigationRailShown = false
        )
    }
}