package com.drsapps.trackerforlegominifiguresseries.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.drsapps.trackerforlegominifiguresseries.domain.model.AppUsage
import com.drsapps.trackerforlegominifiguresseries.domain.repository.AppUsageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import java.io.IOException

private const val TAG = "AppUsageRepositoryImpl"

class AppUsageRepositoryImpl(
    private val dataStore: DataStore<AppUsage>
) : AppUsageRepository {
    override fun getAppUsage(): Flow<AppUsage> {
        return dataStore.data
    }

    override suspend fun incrementAppOpenCount() {
        try {
            dataStore.updateData {
                it.copy(appOpenCount = it.appOpenCount + 1)
            }
        } catch (e: IOException) {
            Log.e(TAG, "incrementAppOpenCount: Failed to write data to the disk.\n" +
                    e.stackTraceToString()
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "incrementAppOpenCount:\n${e.stackTraceToString()}")
        }
    }

    override suspend fun incrementSignificantActionsCount() {
        try {
            dataStore.updateData {
                it.copy(significantActionsCount = it.significantActionsCount + 1)
            }
        } catch (e: IOException) {
            Log.e(TAG, "incrementSignificantActionsCount: Failed to write data to the disk.\n" +
                    e.stackTraceToString()
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "incrementSignificantActionsCount: ${e.stackTraceToString()}")
        }
    }
}