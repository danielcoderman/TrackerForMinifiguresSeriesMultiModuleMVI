package com.drsapps.trackerforlegominifiguresseries.domain.model

import android.util.Log
import androidx.datastore.core.Serializer
import com.drsapps.trackerforlegominifiguresseries.presentation.util.Layout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class UserPreferences(
    val layout: Layout = Layout.GRID
)

private const val TAG = "UserPreferencesSerializer"

// UserPreferences Serializer for DataStore
@Singleton
class UserPreferencesSerializer @Inject constructor(
    private val json: Json
) : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            json.decodeFromString(
                UserPreferences.serializer(), input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "readFrom: ${e.stackTraceToString()}")
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        try {
            withContext(Dispatchers.IO) {
                output.write(
                    json.encodeToString(UserPreferences.serializer(), t).encodeToByteArray()
                )
            }
        } catch (e: Exception) {
            if (e is IOException) {
                Log.e(TAG, "writeTo: I/O error occurred.\n${e.stackTraceToString()}")
            }
            throw e
        }
    }
}
