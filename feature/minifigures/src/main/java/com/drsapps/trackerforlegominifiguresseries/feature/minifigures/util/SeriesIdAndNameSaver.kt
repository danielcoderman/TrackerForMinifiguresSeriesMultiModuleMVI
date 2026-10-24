package com.drsapps.trackerforlegominifiguresseries.feature.minifigures.util

import androidx.compose.runtime.saveable.listSaver
import com.drsapps.trackerforlegominifiguresseries.domain.model.SeriesIdAndName

// Split out of SeriesIdAndName itself (core/model) because core/model is a plain Kotlin module
// with no Compose dependency - SeriesIdAndName is a plain data class, this Saver is the
// Compose-specific adapter for it, kept where Compose already lives.
val SeriesIdAndNameSaver = listSaver<SeriesIdAndName, Any>(
    save = {
        listOf(it.id, it.name)
    },
    restore = {
        SeriesIdAndName(it[0] as Int, it[1] as String)
    }
)
