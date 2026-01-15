package com.drsapps.trackerforlegominifiguresseries.data.remote.dto

import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialSeries
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeriesDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("num_of_minifigs") val numOfMinifigs: Int,
    @SerialName("release_date") val releaseDate: String
)

fun SeriesDto.toPartialSeries(): PartialSeries {
    return PartialSeries(
        id = id,
        name = name,
        imageUrl = imageUrl,
        numOfMinifigs = numOfMinifigs,
        releaseDate = releaseDate
    )
}
