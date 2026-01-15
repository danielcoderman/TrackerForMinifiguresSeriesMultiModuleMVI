package com.drsapps.trackerforlegominifiguresseries.data.remote

import com.drsapps.trackerforlegominifiguresseries.data.remote.dto.MinifigureDto
import com.drsapps.trackerforlegominifiguresseries.data.remote.dto.MinifigureInventoryItemDto
import com.drsapps.trackerforlegominifiguresseries.data.remote.dto.SeriesDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// HTTP client for the series minifig catalog backend
interface SeriesMinifigCatalogApi {

    @GET("series")
    suspend fun getSeries(
        @Query("lastFetched") lastFetched: String? = null  // Note: lastFetched implicitly refers to the last successful fetch where the fetched data was saved locally.
    ): Response<List<SeriesDto>>

    @GET("minifigures")
    suspend fun getMinifigures(
        @Query("lastFetched") lastFetched: String? = null
    ): Response<List<MinifigureDto>>

    @GET("minifigure-inventory-items")
    suspend fun getMinifigureInventoryItems(
        @Query("lastFetched") lastFetched: String? = null
    ): Response<List<MinifigureInventoryItemDto>>

    companion object {
        const val BASE_URL = "[REDACTED]"
    }
}