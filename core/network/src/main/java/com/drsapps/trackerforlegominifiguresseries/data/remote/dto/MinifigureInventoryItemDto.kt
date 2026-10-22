package com.drsapps.trackerforlegominifiguresseries.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinifigureInventoryItemDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("part_url") val partUrl: String,
    @SerialName("minifigure_id") val minifigureId: Int,
    @SerialName("quantity") val quantity: Int,
    @SerialName("type") val type: String,
    @SerialName("set_num") val setNum: String,
    @SerialName("rebrickable_id") val rebrickableId: Int
)
