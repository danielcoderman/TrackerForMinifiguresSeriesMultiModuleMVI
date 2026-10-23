package com.drsapps.trackerforlegominifiguresseries.domain.model

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class AppUsage(
    val appOpenCount: Int = 0,
    val significantActionsCount: Int = 0
)

private const val TAG = "AppUsageSerializer"

// AppUsage Serializer for DataStore
@Singleton
class AppUsageSerializer @Inject constructor(
    private val json: Json
) : Serializer<AppUsage> {
    override val defaultValue = AppUsage()

    override suspend fun readFrom(input: InputStream): AppUsage {
        return try {
            json.decodeFromString(
                AppUsage.serializer(), input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "readFrom: ${e.stackTraceToString()}")
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppUsage, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                json.encodeToString(AppUsage.serializer(), t).encodeToByteArray()
            )
        }
    }
}
