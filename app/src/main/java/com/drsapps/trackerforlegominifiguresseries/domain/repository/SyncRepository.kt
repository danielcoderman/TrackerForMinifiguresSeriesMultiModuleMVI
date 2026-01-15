package com.drsapps.trackerforlegominifiguresseries.domain.repository

import com.drsapps.trackerforlegominifiguresseries.domain.model.AppState
import com.drsapps.trackerforlegominifiguresseries.util.SyncState
import kotlinx.datetime.Instant

// This is basically a fetch and save repository or simply said, a sync repository.
interface SyncRepository {

    // This function syncs local data with remote series, minifigure, and minifigure inventory item
    // data from the series minifig catalog backend
    suspend fun syncData(
        lastFetchedTimestamp: Instant,
        updateLastFetchedTimestamp: suspend (timestamp: Instant) -> Unit,
        getAppState: suspend () -> AppState
    ): SyncState

}
