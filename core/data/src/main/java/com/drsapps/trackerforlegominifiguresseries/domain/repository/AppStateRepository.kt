package com.drsapps.trackerforlegominifiguresseries.domain.repository

import com.drsapps.trackerforlegominifiguresseries.domain.model.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface AppStateRepository {
    fun getAppStateFlow(): Flow<AppState>

    suspend fun getAppState(): AppState

    suspend fun updateLastReviewRequestTimestamp(timestamp: Instant)

    suspend fun updateLastFetchedTimestamp(timestamp: Instant)

    suspend fun updateHasExistingDataValue(value: Boolean)
}
