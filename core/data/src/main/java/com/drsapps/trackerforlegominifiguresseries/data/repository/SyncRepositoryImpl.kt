package com.drsapps.trackerforlegominifiguresseries.data.repository

import android.util.Log
import com.drsapps.trackerforlegominifiguresseries.data.local.UpsertDao
import com.drsapps.trackerforlegominifiguresseries.data.remote.SeriesMinifigCatalogApi
import com.drsapps.trackerforlegominifiguresseries.data.remote.dto.MinifigureDto
import com.drsapps.trackerforlegominifiguresseries.data.remote.dto.MinifigureInventoryItemDto
import com.drsapps.trackerforlegominifiguresseries.data.remote.dto.SeriesDto
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppState
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialMinifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialMinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialSeries
import com.drsapps.trackerforlegominifiguresseries.domain.repository.SyncRepository
import com.drsapps.trackerforlegominifiguresseries.util.SyncState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "SyncRepositoryImpl"

// This is basically a fetch and save repository or simply said, a sync repository.
class SyncRepositoryImpl(
    private val seriesMinifigCatalogApi: SeriesMinifigCatalogApi,
    private val upsertDao: UpsertDao
) : SyncRepository {

    // Prevents concurrent data syncs. Mainly we're preventing concurrent syncs caused by
    // manual pull-to-refresh and the execution of the data sync worker. Why? Because other
    // potential concurrent syncs are already handled, like the initial data sync in the splash
    // screen and the data sync worker. In this case the data sync worker won't be scheduled, much
    // less executed, because the user has no existing data and the splash screen will handle giving
    // the user their initial data.
    private val syncMutex = Mutex()

    override suspend fun syncData(
        lastFetchedTimestamp: Instant,
        updateLastFetchedTimestamp: suspend (timestamp: Instant) -> Unit,
        getAppState: suspend () -> AppState
    ): SyncState {
        return syncMutex.withLock {
            val latestAppState = getAppState()
            if (latestAppState.lastFetchedTimestamp > lastFetchedTimestamp) {
                Log.d(TAG, "Sync skipped. Already performed successfully by a would-be concurrent data sync.")
                SyncState.NoNewData
            } else {
                try {
                    coroutineScope {
                        // Track the current timestamp right before the fetch for data
                        val currentFetchTimestamp = Clock.System.now()

                        // Fetch series, minifigure, and minifigure inventory item data (It would be safer to make an individual call to the REST API instead of three. That way there would be one transaction instead of three separate reading transactions)
                        val seriesDeferred = async(Dispatchers.IO) { seriesMinifigCatalogApi.getSeries(lastFetchedTimestamp.toString()) }
                        val minifiguresDeferred = async(Dispatchers.IO) { seriesMinifigCatalogApi.getMinifigures(lastFetchedTimestamp.toString()) }
                        val minifigureinventoryItemsDeferred = async(Dispatchers.IO) { seriesMinifigCatalogApi.getMinifigureInventoryItems(lastFetchedTimestamp.toString()) }

                        // Wait for all requests to complete
                        val seriesResponse = seriesDeferred.await()
                        val minifiguresResponse = minifiguresDeferred.await()
                        val minifigureInventoryItemsResponse = minifigureinventoryItemsDeferred.await()

                        // Validate the responses
                        if (seriesResponse.isSuccessful &&
                            minifiguresResponse.isSuccessful &&
                            minifigureInventoryItemsResponse.isSuccessful &&
                            seriesResponse.body() != null &&
                            minifiguresResponse.body() != null && // Note: If there is no new data to fetch an empty list will be returned (not null).
                            minifigureInventoryItemsResponse.body() != null) {
                            // Check if the response bodies (lists of data) are empty which would mean
                            // that there's no new data
                            if (seriesResponse.body()!!.isEmpty() &&
                                minifiguresResponse.body()!!.isEmpty() &&
                                minifigureInventoryItemsResponse.body()!!.isEmpty()
                            ) {
                                // Update the last fetched timestamp because the fetch was successful
                                // even if there was no new data.
                                updateLastFetchedTimestamp(currentFetchTimestamp)

                                SyncState.NoNewData
                            } else {
                                // Map the response data to the relevant partial entities
                                val series = seriesResponse.body()!!.map {
                                    it.toPartialSeries()
                                }
                                val minifigures = minifiguresResponse.body()!!.map {
                                    it.toPartialMinifigure()
                                }
                                val minifigureInventoryItems = minifigureInventoryItemsResponse.body()!!.map {
                                    it.toPartialMinifigureInventoryItem()
                                }

                                // Insert or update the data in the local Room database
                                upsertDao.upsertAll(
                                    series = series,
                                    minifigures = minifigures,
                                    minifigInventoryItems = minifigureInventoryItems
                                )

                                // Update the last fetched timestamp since the fetch and save was successful
                                updateLastFetchedTimestamp(currentFetchTimestamp)

                                SyncState.Success
                            }
                        } else {
                            Log.e(TAG, "syncData: Network response error")
                            SyncState.Failure("Network response error")
                        }
                    }
                } catch (e: UnknownHostException) {
                    Log.e(TAG, "syncData: Error likely due to no internet connection\n${e.stackTraceToString()}")
                    SyncState.Failure("No internet connection")
                } catch (e: SocketTimeoutException) {
                    Log.e(TAG, "syncData: Timeout likely due to slow server startup\n${e.stackTraceToString()}")
                    SyncState.Failure("Connection timeout - please try again")
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    Log.e(TAG, "syncData: ${e.stackTraceToString()}")
                    SyncState.Failure("Sync failed. Please check your internet.")
                }
            }
        }
    }
}

// Mapping between network DTOs and the Room "Partial" upsert types. Lives here, next to
// SyncRepositoryImpl (the only caller), because core/network and core/database are siblings -
// neither should depend on the other - and this is exactly the cross-cutting concern core/data
// will own once it's extracted.
private fun SeriesDto.toPartialSeries(): PartialSeries {
    return PartialSeries(
        id = id,
        name = name,
        imageUrl = imageUrl,
        numOfMinifigs = numOfMinifigs,
        releaseDate = releaseDate
    )
}

private fun MinifigureDto.toPartialMinifigure(): PartialMinifigure {
    return PartialMinifigure(
        id = id,
        name = name,
        imageUrl = imageUrl,
        positionInSeries = positionInSeries,
        seriesId = seriesId,
        inventorySize = inventorySize
    )
}

private fun MinifigureInventoryItemDto.toPartialMinifigureInventoryItem(): PartialMinifigureInventoryItem {
    return PartialMinifigureInventoryItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        partUrl = partUrl,
        quantity = quantity,
        type = type,
        minifigureId = minifigureId
    )
}

// TODO("Consider if you should send a personalized message like 'Too many api requests in window' when the user makes more than 100 requests in a 15 minute window. I think this would fall under the network response error currently.")
