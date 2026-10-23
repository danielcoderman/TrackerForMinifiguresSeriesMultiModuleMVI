package com.drsapps.trackerforlegominifiguresseries.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.drsapps.trackerforlegominifiguresseries.domain.model.UserPreferences
import com.drsapps.trackerforlegominifiguresseries.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException

private const val TAG = "UserPreferencesRepositoryImpl"

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<UserPreferences>
) : UserPreferencesRepository {

    override fun getUserPreferences(): Flow<UserPreferences> {
        return try {
            dataStore.data
        } catch (e: Exception) {
            if (e is IOException) {
                Log.e(TAG, "getLayout: Failed to read from disk.\n${e.stackTraceToString()}")
            }
            throw e
        }
    }

    override suspend fun setUserPreferences(userPref: UserPreferences) {
        dataStore.updateData {
            userPref
        }
    }

}