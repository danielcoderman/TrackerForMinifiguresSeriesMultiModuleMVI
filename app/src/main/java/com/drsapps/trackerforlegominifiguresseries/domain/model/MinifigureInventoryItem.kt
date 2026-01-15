package com.drsapps.trackerforlegominifiguresseries.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Note: The underlying MinifigureInventoryItem table mapping to the MinifigureInventoryItem entity
// above marks all columns with the NOT NULL constraint. Take a look at the json file in the schemas
// folder to see that the columns are marked with the NOT NULL constraint.

// Note: Using the @ColumnInfo annotation lets the underlying MinifigureInventoryItem table know the
// default value for any items inserted in the table. This would be useful when inserting a
// minifigure inventory item in the database using @Query (raw sqlite) instead of @Insert.
// https://stackoverflow.com/questions/47905627/how-to-annotate-a-default-value-inside-a-android-room-entity

// Note: The underlying MinifigureInventoryItem table is a SQLite table
// (Room uses SQLite under the hood) and there is no Boolean type in SQLite, so an Integer type is
// used instead with values 0 and 1 as a replacement.

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Minifigure::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("minifigureId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = false // Non-urgent: Setting this to true is useful for batch inserts into a database in a single transaction. In my app this may only occur when importing backed up data into the database.
        )
    ]
)
data class MinifigureInventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imageUrl: String,
    val partUrl: String,
    val quantity: Int,
    val type: String,
    @ColumnInfo(index = true)
    val minifigureId: Int,
    @ColumnInfo(defaultValue = "0")
    val collected: Boolean
)

// This is used to update certain columns of minifigure inventory items in the MinifigureInventoryItem table when updating them using
// @Upsert
data class PartialMinifigureInventoryItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val partUrl: String,
    val quantity: Int,
    val type: String,
    val minifigureId: Int
)