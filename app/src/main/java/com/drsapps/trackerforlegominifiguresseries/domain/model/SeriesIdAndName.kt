package com.drsapps.trackerforlegominifiguresseries.domain.model

import androidx.compose.runtime.saveable.listSaver

data class SeriesIdAndName(
    val id: Int,
    val name: String
) {
    companion object {
        val saver = listSaver<SeriesIdAndName, Any>(
            save = {
                listOf(it.id, it.name)
            },
            restore = {
                SeriesIdAndName(it[0] as Int, it[1] as String)
            }
        )
    }
}