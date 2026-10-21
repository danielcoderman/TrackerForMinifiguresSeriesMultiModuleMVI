package com.drsapps.trackerforlegominifiguresseries.presentation.series

import com.drsapps.trackerforlegominifiguresseries.domain.model.Series
import com.drsapps.trackerforlegominifiguresseries.presentation.util.SeriesFilter

// The single snapshot of everything SeriesScreen needs to render. This will replace the four
// separate StateFlows (filter, series, isDataRefreshing, refreshMessage) currently exposed by
// SeriesViewModel. Note that refreshMessage is intentionally NOT part of this state - see
// SeriesEffect below.
data class SeriesState(
    val series: List<Series> = emptyList(),
    val filter: SeriesFilter = SeriesFilter.ALL,
    val isRefreshing: Boolean = false
)

// Everything the user can do on this screen. This will replace SeriesViewModel's separate
// public methods (changeFilterAndSeries, refreshData) with a single onEvent(event) entry point.
sealed interface SeriesEvent {
    data class FilterChanged(val filter: SeriesFilter) : SeriesEvent
    data object Refresh : SeriesEvent
}

// One-shot outcomes that aren't part of the durable UI state - showing them again on
// rotation or screen re-subscription would be wrong. This will replace the refreshMessage
// String currently living in state, along with the DisposableEffect workaround used to clear
// it when the screen leaves composition.
sealed interface SeriesEffect {
    data class ShowSnackbar(val message: String) : SeriesEffect
}
