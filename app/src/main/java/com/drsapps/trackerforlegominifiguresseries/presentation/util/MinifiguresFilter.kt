package com.drsapps.trackerforlegominifiguresseries.presentation.util

enum class MinifiguresFilter(val displayName: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    COLLECTED("Collected"),
    WISHLIST("Wishlist"),
    UNCOLLECTED("Uncollected")
}

// Non-urgent: Consider using EnumClass.entries() instead
val minifiguresFilters = listOf(
    MinifiguresFilter.ALL,
    MinifiguresFilter.FAVORITES,
    MinifiguresFilter.COLLECTED,
    MinifiguresFilter.WISHLIST,
    MinifiguresFilter.UNCOLLECTED
)