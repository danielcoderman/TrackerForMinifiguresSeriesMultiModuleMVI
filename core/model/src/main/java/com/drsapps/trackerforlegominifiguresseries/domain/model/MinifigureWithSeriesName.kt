package com.drsapps.trackerforlegominifiguresseries.domain.model

data class MinifigureWithSeriesName(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val collected: Boolean,
    val wishListed: Boolean,
    val favorite: Boolean,
    val seriesName: String
)
