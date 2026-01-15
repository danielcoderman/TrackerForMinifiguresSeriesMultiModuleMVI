package com.drsapps.trackerforlegominifiguresseries.presentation.minifigures.components

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
import com.drsapps.trackerforlegominifiguresseries.presentation.util.MinifiguresFilter
import com.drsapps.trackerforlegominifiguresseries.presentation.util.minifiguresFilters
import com.drsapps.trackerforlegominifiguresseries.ui.theme.TrackerForLegoMinifiguresSeriesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinifiguresFiltersScrollableTabRow(
    filter: MinifiguresFilter,
    resetScrollingPosition: suspend () -> Unit,
    changeFilterAndMinifigures: (MinifiguresFilter) -> Unit,
    isNavigationRailShown: Boolean
) {
    // Creates a CoroutineScope bound to the MinifiguresFiltersScrollableTabRow's lifecycle
    val coroutineScope = rememberCoroutineScope()

    val selectedTabIndex = filter.ordinal

    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            divider = {}
        ) {
            minifiguresFilters.forEach { minifiguresFilter ->
                val isSelected = minifiguresFilter.ordinal == selectedTabIndex
                Tab(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            coroutineScope.launch {
                                resetScrollingPosition()
                            }
                            changeFilterAndMinifigures(minifiguresFilter)
                        }
                    },
                    text = {
                        Text(minifiguresFilter.displayName)
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
fun MinifiguresFiltersScrollableTabRowPreview() {
    TrackerForLegoMinifiguresSeriesTheme {
        MinifiguresFiltersScrollableTabRow(
            filter = MinifiguresFilter.FAVORITES,
            resetScrollingPosition = {},
            changeFilterAndMinifigures = {},
            isNavigationRailShown = false
        )
    }
}