package com.drsapps.trackerforlegominifiguresseries.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppState
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppStateRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import java.io.IOException

private const val TAG = "AppStateRepositoryImpl"

class AppStateRepositoryImpl(
    private val dataStore: DataStore<AppState>
) : AppStateRepository {
    override fun getAppStateFlow(): Flow<AppState> {
        return dataStore.data
    }

    override suspend fun getAppState(): AppState {
        return dataStore.data.first()
    }

    override suspend fun updateLastReviewRequestTimestamp(timestamp: Instant) {
        try {
            dataStore.updateData {
                it.copy(lastReviewRequestTimestamp = timestamp)
            }
        } catch (e: IOException) {
            Log.e(TAG, "updateLastReviewRequest: Failed to write data to the disk.\n" +
                    e.stackTraceToString()
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "updateLastReviewRequest: ${e.stackTraceToString()}")
        }
    }

    override suspend fun updateLastFetchedTimestamp(timestamp: Instant) {
        try {
            dataStore.updateData {
                it.copy(lastFetchedTimestamp = timestamp)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "updateLastFetchedTimestamp: ${e.stackTraceToString()}")
        }
    }

    override suspend fun updateHasExistingDataValue(value: Boolean) {
        try {
            dataStore.updateData {
                it.copy(hasExistingData = value)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "updateHasExistingDataValue: ${e.stackTraceToString()}")
        }
    }
}
