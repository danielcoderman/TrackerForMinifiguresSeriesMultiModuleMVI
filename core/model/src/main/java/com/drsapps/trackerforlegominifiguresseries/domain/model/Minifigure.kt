package com.drsapps.trackerforlegominifiguresseries.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

// Note: The underlying Minifigure table mapping to the Minifigure entity above marks all columns
// with the NOT NULL constraint. Take a look at the json file in the schemas folder to see that
// the columns are marked with the NOT NULL constraint.

// Note: Using the @ColumnInfo annotation lets the underlying Minifigure table know the default value
// for any minifigures inserted in the table. This would be useful when inserting a minifigure
// in the database using @Query instead of @Insert.
// https://stackoverflow.com/questions/47905627/how-to-annotate-a-default-value-inside-a-android-room-entity

// Note: The underlying Minifigure table is a SQLite table (Room uses SQLite under the hood) and
// there is no Boolean type in SQLite, so an Integer type is used instead with values 0 and 1 as a
// replacement.

@Entity(
    indices = [Index(value = ["imageUrl"], unique = true)], // Note: A plain UNIQUE constraint on a column, other than via an index, is not supported. Therefore, this is the only option to mark the imageUrl column as unique.
    foreignKeys = [
        ForeignKey(
            entity = Series::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("seriesId"),
            onDelete = CASCADE,
            onUpdate = CASCADE,
            deferred = false // Non-urgent: Setting this to true is useful for batch inserts into a database in a single transaction. In my app this may only occur when importing backed up data into the database.
        )
    ]
)
data class Minifigure(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imageUrl: String,
    val positionInSeries: Int,
    @ColumnInfo(index = true)
    val seriesId: Int,
    @ColumnInfo(defaultValue = "0")
    val collected: Boolean,
    @ColumnInfo(defaultValue = "0")
    val wishListed: Boolean,
    @ColumnInfo(defaultValue = "0")
    val favorite: Boolean,
    @ColumnInfo(defaultValue = "0")
    val hidden: Boolean,
    @ColumnInfo(defaultValue = "0")
    val numOfCollectedInventoryItems: Int,
    @ColumnInfo(defaultValue = "-1") // This default value is only necessary so that existing users (with existing data in the Minifigure table) can have the "NOT NULL" constraint added to this field. Otherwise, this field wouldn't need a default value.
    val inventorySize: Int
)

// This is used to update certain columns of minifigures in the Minifigure table when updating them using
// @Upsert
data class PartialMinifigure(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val positionInSeries: Int,
    val seriesId: Int,
    val inventorySize: Int
)