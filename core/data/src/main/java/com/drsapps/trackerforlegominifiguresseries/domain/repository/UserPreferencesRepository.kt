package com.drsapps.trackerforlegominifiguresseries.domain.repository

import com.drsapps.trackerforlegominifiguresseries.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getUserPreferences(): Flow<UserPreferences>

    suspend fun setUserPreferences(userPref: UserPreferences)
}