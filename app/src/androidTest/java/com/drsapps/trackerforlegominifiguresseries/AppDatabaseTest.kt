package com.drsapps.trackerforlegominifiguresseries

import android.content.Context
import androidx.datastore.core.IOException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.drsapps.trackerforlegominifiguresseries.data.local.SeriesDao
import com.drsapps.trackerforlegominifiguresseries.data.local.TrackerForLegoMinifiguresSeriesDatabase
import com.drsapps.trackerforlegominifiguresseries.domain.model.PartialSeries
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var seriesDao: SeriesDao
    private lateinit var db: TrackerForLegoMinifiguresSeriesDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TrackerForLegoMinifiguresSeriesDatabase::class.java).build()
        seriesDao = db.getSeriesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // TODO: Refactor and improve this test
    @Test
    @Throws(Exception::class)
    fun seriesUpsertTest() = runTest {
        // Insert a series into the database
        db.openHelper.writableDatabase.execSQL("""
            INSERT INTO Series (name, imageUrl, numOfMinifigs, releaseDate, favorite, numOfMinifigsCollected)
            VALUES ('Series 1', 'url1', 16, '2010-05-01', 1, 2)
        """.trimIndent())

        db.openHelper.readableDatabase.query("SELECT * FROM Series").use { cursor ->
            assertEquals(true, cursor.moveToFirst())
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("id")))
            assertEquals("Series 1", cursor.getString(cursor.getColumnIndexOrThrow("name")))
            assertEquals("url1", cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")))
            assertEquals(16, cursor.getInt(cursor.getColumnIndexOrThrow("numOfMinifigs")))
            assertEquals("2010-05-01", cursor.getString(cursor.getColumnIndexOrThrow("releaseDate")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("favorite")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("numOfMinifigsCollected")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("numOfMinifigsHidden")))
        }

        // This is a new series to insert with its id purposely set to 3
        val series = PartialSeries(
            id = 3,
            name = "Series 3",
            imageUrl = "url3",
            numOfMinifigs = 16,
            releaseDate = "2011-01-01"
        )

        // This is an existing series to test the update functionality of the @Upsert method
        val series2 = PartialSeries(
            id = 1,
            name = "Series 1 - Party",
            imageUrl = "url1.1",
            numOfMinifigs = 16,
            releaseDate = "2011-01-01"
        )
//        seriesDao.insertOrUpdateSeries(listOf(series, series2))

        // Inserting another series to see if the id will be 4
        db.openHelper.writableDatabase.execSQL("""
            INSERT INTO Series (name, imageUrl, numOfMinifigs, releaseDate, favorite, numOfMinifigsCollected)
            VALUES ('Series 5', 'url5', 16, '2015-09-01', 0, 5)
        """.trimIndent())

        val visibleSeries = seriesDao.getVisibleSeries().first()
        assertEquals(3, visibleSeries.size)
        assertEquals("Series 1 - Party", visibleSeries.first { it.id == 1 }.name)
        assertEquals("Series 3", visibleSeries.first { it.id == 3 }.name)
        assertEquals("Series 5", visibleSeries.first {it.id == 4}.name)
        println("series list: $visibleSeries")
    }
}