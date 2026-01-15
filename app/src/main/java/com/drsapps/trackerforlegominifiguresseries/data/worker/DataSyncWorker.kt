package com.drsapps.trackerforlegominifiguresseries.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.drsapps.trackerforlegominifiguresseries.domain.use_case.SyncDataUseCase
import com.drsapps.trackerforlegominifiguresseries.util.SyncState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "DataSyncWorker"

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncDataUseCase: SyncDataUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting background data sync")

            when (val syncResult = syncDataUseCase()) {
                is SyncState.Success -> {
                    Log.d(TAG, "Background data sync completed successfully")
                    Result.success()
                }
                is SyncState.NoNewData -> {
                    Log.d(TAG, "Background data sync: No new data available")
                    Result.success()
                }
                is SyncState.Failure -> {
                    Log.e(TAG, "Background data sync failed: ${syncResult.message}")
                    Result.failure()
                }
                else -> {
                    Log.e(TAG, "This branch shouldn't have executed because syncDataUseCase doesn't return SyncState.Loading.")
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't sync the data in the background: ${e.stackTraceToString()}")
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "data_sync_work"
    }
}