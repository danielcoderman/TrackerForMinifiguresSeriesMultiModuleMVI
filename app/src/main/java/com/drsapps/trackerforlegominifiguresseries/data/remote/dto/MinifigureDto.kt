package com.drsapps.trackerforlegominifiguresseries.data.remote.dto

import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialMinifigure
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinifigureDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("position_in_series") val positionInSeries: Int,
    @SerialName("series_id") val seriesId: Int,
    @SerialName("inventory_size") val inventorySize: Int
)

fun MinifigureDto.toPartialMinifigure(): PartialMinifigure {
    return PartialMinifigure(
        id = id,
        name = name,
        imageUrl = imageUrl,
        positionInSeries = positionInSeries,
        seriesId = seriesId,
        inventorySize = inventorySize
    )
}