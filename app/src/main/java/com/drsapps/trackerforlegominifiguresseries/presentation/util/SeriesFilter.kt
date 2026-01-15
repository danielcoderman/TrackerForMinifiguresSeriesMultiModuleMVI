package com.drsapps.trackerforlegominifiguresseries.presentation.util

enum class SeriesFilter(val displayName: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    COMPLETED("Completed"),
    UNCOMPLETED("Uncompleted")
}

// Non-urgent: Consider using EnumClass.entries() instead
val seriesFilters = listOf(
    SeriesFilter.ALL,
    SeriesFilter.FAVORITES,
    SeriesFilter.COMPLETED,
    SeriesFilter.UNCOMPLETED
)