package com.drsapps.trackerforlegominifiguresseries.domain.model

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class AppState(
    val lastReviewRequestTimestamp: Instant = Instant.DISTANT_PAST,
    val lastFetchedTimestamp: Instant = Instant.fromEpochMilliseconds(0), // [1]
    val hasExistingData: Boolean? = null
)

private const val TAG = "AppStateSerializer"

// AppState Serializer for DataStore
@Singleton
class AppStateSerializer @Inject constructor(
    private val json: Json
) : Serializer<AppState> {
    override val defaultValue = AppState()

    override suspend fun readFrom(input: InputStream): AppState {
        return try {
            json.decodeFromString(
                AppState.serializer(), input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            // Don't eat up the CancellationException error, this could cause issues
            if (e is CancellationException) throw e

            Log.e(TAG, "readFrom: ${e.stackTraceToString()}")
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppState, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                json.encodeToString(AppState.serializer(), t).encodeToByteArray()
            )
        }
    }
}

// Notes
//
// It appears to be safe to add new properties to the AppState data class, even for existing
// users. The reason is because Kotlin serialization, when it deserializes the stored json of the
// previous version of the data class, will use the default values provided in the updated data class
// for any missing properties. However, for this to work make sure that the new properties have
// default values.
// https://chatgpt.com/share/686c2fe1-d290-800f-a719-2234e92fc4a0)
//
// Note that because ignoreUnknownKeys is enabled on the injected Json instance, removing
// properties from AppState remains backwards compatible for existing users (i.e., there are no crashes).
// According to Codex the removed property will be removed from disk once AppState is serialized and
// written to the disk. Before this write the the removed property will exist on disk and it will be
// read, but the AppState used by the app will not include it (it will be ignored).
//
// [1]: Using Instant.DISTANT_PAST doesn't work as the default for the lastFetchedTimestamp. The
// reason is because the series minifig catalog REST API parses and verifies the the last fetched
// timestamp, and Instant.DISTANT_PAST doesn't appear to be a real time in the past. As a ISO 8601
// string it looks like "-100001-12-31T23:59:59.999999999Z".
