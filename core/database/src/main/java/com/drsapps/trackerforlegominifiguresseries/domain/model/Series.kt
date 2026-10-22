package com.drsapps.trackerforlegominifiguresseries.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Note: The underlying Series table mapping to the Series entity above marks all columns
// with the NOT NULL constraint. Take a look at the json file in the schemas folder to see that
// the columns are marked with the NOT NULL constraint.

// Note: Using the @ColumnInfo annotation lets the underlying Series table know the default values
// for any series inserted into the table. This would be useful when inserting a series
// in the database using @Query instead of @Insert.
// https://stackoverflow.com/questions/47905627/how-to-annotate-a-default-value-inside-a-android-room-entity

// Note: The underlying Series table is a SQLite table (Room uses SQLite under the hood) and
// there is no Boolean type in SQLite, so an Integer type is used instead with values 0 and 1 as a
// replacement.

@Entity(
    indices = [Index(value = ["imageUrl"], unique = true)], // Note: A plain UNIQUE constraint on a column, other than via an index, is not supported. Therefore, this is the only option to mark the imageUrl column as unique.
)
data class Series(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imageUrl: String,
    val numOfMinifigs: Int,
    val releaseDate: String,
    @ColumnInfo(defaultValue = "0")
    val favorite: Boolean,
    @ColumnInfo(defaultValue = "0")
    val numOfMinifigsCollected: Int,
    @ColumnInfo(defaultValue = "0")
    val numOfMinifigsHidden: Int
)

// This is used to update certain columns of series in the Series table when updating them using
// @Upsert
data class PartialSeries(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val numOfMinifigs: Int,
    val releaseDate: String
)

