package com.drsapps.trackerforlegominifiguresseries

import androidx.datastore.core.IOException
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.drsapps.trackerforlegominifiguresseries.data.local.TrackerForLegoMinifiguresSeriesDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DB = "migration-test"

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

//    // Array of all migrations
//    private val allMigrations = arrayOf(
//        TrackerForLegoMinifiguresSeriesDatabase.migration1To2,
//        TrackerForLegoMinifiguresSeriesDatabase.migration2To3,
//        TrackerForLegoMinifiguresSeriesDatabase.migration1To4,
//        TrackerForLegoMinifiguresSeriesDatabase.migration2to4,
//        TrackerForLegoMinifiguresSeriesDatabase.migration3to4
//    )

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = TrackerForLegoMinifiguresSeriesDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migration1To2_containsCorrectData() {
        // Create the database in version 1
        helper.createDatabase(TEST_DB, 1).apply {
            // Insert some data
            execSQL("""
                INSERT INTO Series (id, name, imageUrl, numOfMinifigs, releaseDate, favorite)
                VALUES (45, 'Series 26 Space', 'url0', 12, '2024-05-01', 1)
            """.trimIndent())
            execSQL("""
                INSERT INTO Minifigure (name, imageUrl, positionInSeries, seriesId, collected)
                VALUES ('Spacewalking Astronaut', 'url0', 1, 45, 1)
            """.trimIndent())
            // Prepare for the next version
            close()
        }

        // Re-open the database with version 2, but first provide the migration1To2 as the migration process.
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, TrackerForLegoMinifiguresSeriesDatabase.migration1To2)

        // MigrationTestHelper automatically verifies the schema changes, but not the data validity.
        // Validate that the data was migrated properly.
        db.query("SELECT * FROM Series").apply {
            // Test that the series data in the previous database is preserved
            assertEquals(true, moveToFirst())
            assertEquals(45, getInt(getColumnIndexOrThrow("id")))
            assertEquals("Series 26 Space", getString(getColumnIndexOrThrow("name")))
            assertEquals(1, getInt(getColumnIndexOrThrow("favorite")))

            // Test that the new series data from the migration is present in the database
            assertEquals(true, moveToNext())
            assertEquals(46, getInt(getColumnIndexOrThrow("id")))
            assertEquals("Dungeons & Dragons", getString(getColumnIndexOrThrow("name")))
        }

        db.query("SELECT * FROM Minifigure").apply {
            // Test that the minifigure data in the previous database is preserved
            assertEquals(true, moveToFirst())
            assertEquals("Spacewalking Astronaut", getString(getColumnIndexOrThrow("name")))
            assertEquals(1, getInt(getColumnIndexOrThrow("collected")))

            // Test that the new minifigure data from the migration is present in the database.
            // There are 12 new minifigures, so to not have to check that all are present,
            // I will check the beginning, middle, and end minifigures.
            // Beginning
            assertEquals(true, moveToPosition(1))
            assertEquals("Dwarf Barbarian", getString(getColumnIndexOrThrow("name")))
            assertEquals(46, getInt(getColumnIndexOrThrow("seriesId")))
            // Middle
            assertEquals(true, moveToPosition(6))
            assertEquals("Aarakocra Ranger", getString(getColumnIndexOrThrow("name")))
            assertEquals(46, getInt(getColumnIndexOrThrow("seriesId")))
            // End
            assertEquals(true, moveToPosition(12))
            assertEquals("Tasha the Witch Queen", getString(getColumnIndexOrThrow("name")))
            assertEquals(46, getInt(getColumnIndexOrThrow("seriesId")))
        }

        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun migration2To3_containsCorrectData() {
        // Create the database in version 2
        helper.createDatabase(TEST_DB, 2).apply {
            // Insert some data
            execSQL("""
                INSERT INTO Series (id, name, imageUrl, numOfMinifigs, releaseDate, favorite)
                VALUES (46, 'Dungeons & Dragons', 'url0', 12, '2024-09-01', 1)
            """.trimIndent())
            execSQL("""
                INSERT INTO Minifigure (name, imageUrl, positionInSeries, seriesId, collected)
                VALUES ('Dwarf Barbarian', 'url0', 1, 46, 1)
            """.trimIndent())
            // Prepare for the next version
            close()
        }

        // Re-open the database with version 3, but first provide the migration2To3 as the migration process.
        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, TrackerForLegoMinifiguresSeriesDatabase.migration2To3)

        // MigrationTestHelper automatically verifies the schema changes, but not the data validity.
        // Validate that the data was migrated properly.
        db.query("SELECT * FROM Series").apply {
            // Test that the series data in the previous database is preserved
            assertEquals(true, moveToFirst())
            assertEquals(46, getInt(getColumnIndexOrThrow("id")))
            assertEquals("Dungeons & Dragons", getString(getColumnIndexOrThrow("name")))
            assertEquals(1, getInt(getColumnIndexOrThrow("favorite")))

            // Test that the new series data from the migration is present in the database
            assertEquals(true, moveToNext())
            assertEquals(47, getInt(getColumnIndexOrThrow("id")))
            assertEquals("Series 27", getString(getColumnIndexOrThrow("name")))
        }

        db.query("SELECT * FROM Minifigure").apply {
            // Test that the minifigure data in the previous database is preserved
            assertEquals(true, moveToFirst())
            assertEquals("Dwarf Barbarian", getString(getColumnIndexOrThrow("name")))
            assertEquals(1, getInt(getColumnIndexOrThrow("collected")))

            // Test that the new minifigure data from the migration is present in the database.
            // There are 12 new minifigures, so to not have to check that all are present,
            // I will check the beginning, middle, and end minifigures.
            // Beginning
            assertEquals(true, moveToPosition(1))
            assertEquals("Hamster Costume Fan", getString(getColumnIndexOrThrow("name")))
            assertEquals(47, getInt(getColumnIndexOrThrow("seriesId")))
            // Middle
            assertEquals(true, moveToPosition(6))
            assertEquals("Pterodactyl Costume Fan", getString(getColumnIndexOrThrow("name")))
            assertEquals(47, getInt(getColumnIndexOrThrow("seriesId")))
            // End
            assertEquals(true, moveToPosition(12))
            assertEquals("Steampunk Inventor", getString(getColumnIndexOrThrow("name")))
            assertEquals(47, getInt(getColumnIndexOrThrow("seriesId")))
        }

        db.close()
    }

    // General migration to version 4 tests suffice because migrations 1 to 4, 2 to 4, and 3 to 4
    // are the same (This is because there were no schema changes between versions 1, 2, and 3)
    @Test
    @Throws(IOException::class)
    fun migrationTo4_noLostData() {
        // Create the database in version 1
        helper.createDatabase(TEST_DB, 1).apply {
            // Insert some data
            execSQL("""
                INSERT INTO Series (id, name, imageUrl, numOfMinifigs, releaseDate, favorite)
                VALUES (45, 'Series 26 Space', 'url0', 12, '2024-05-01', 1)
            """.trimIndent())
            execSQL("""
                INSERT INTO Minifigure (name, imageUrl, positionInSeries, seriesId, collected)
                VALUES ('Spacewalking Astronaut', 'url0', 1, 45, 1)
            """.trimIndent())
            // Create a trigger called "updateNumOfMinifigsHiddenAndResetSomeStatesOfMinifig"
            // because it's not described in the schema for any past versions (1, 2, and 3) even
            // though it's present in the real database for said versions. I believe room database
            // migration testing relies mainly on the schemas, for example, to create databases.
            // Note that without this trigger a "SQLiteException: no such trigger" error will occur
            // because the migration to version 4 will try to drop it.
            execSQL("""
                CREATE TRIGGER updateNumOfMinifigsHiddenAndResetSomeStatesOfMinifig
                	AFTER UPDATE ON Minifigure
                	WHEN OLD.hidden != NEW.hidden
                		 AND NEW.hidden = 1
                BEGIN	
                	UPDATE Series
                	SET numOfMinifigsHidden = numOfMinifigsHidden + 1
                	WHERE id = NEW.seriesId;

                	UPDATE Minifigure
                	SET collected = 0,
                		wishListed = 0,
                		favorite = 0
                	WHERE id = NEW.id;
                END
            """.trimIndent())
            // Prepare for the next version
            close()
        }

        // Re-open the database with version 4, but first provide the migration1To4 as the migration process.
        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, TrackerForLegoMinifiguresSeriesDatabase.migration1To4)

        // MigrationTestHelper automatically verifies the schema changes, but not the data validity.
        // Validate that the existing data isn't lost.
        db.query("SELECT * FROM Series").apply {
            assertEquals(true, moveToFirst())
            assertEquals(45, getInt(getColumnIndexOrThrow("id")))
            assertEquals("Series 26 Space", getString(getColumnIndexOrThrow("name")))
            assertEquals(1, getInt(getColumnIndexOrThrow("favorite")))
        }
        db.query("SELECT * FROM Minifigure").apply {
            assertEquals(true, moveToFirst())
            assertEquals("Spacewalking Astronaut", getString(getColumnIndexOrThrow("name")))
            assertEquals(1, getInt(getColumnIndexOrThrow("collected")))
        }

        db.close()
    }

    // This test checks that the new triggers are present after the migration to version 4 and that
    // they behave as expected. It also verifies that a certain trigger was deleted.
    @Test
    @Throws(IOException::class)
    fun migrationTo4_containsNewTriggersAndCorrectBehavior() {
        // Create the database in version 1
        helper.createDatabase(TEST_DB, 1).apply {
            // Insert some data
            execSQL("""
                INSERT INTO Series (name, imageUrl, numOfMinifigs, releaseDate)
                VALUES ('Series 1', 'url1', 2, '2010-05-01')
            """.trimIndent())
            execSQL("""
                INSERT INTO Minifigure (name, imageUrl, positionInSeries, seriesId)
                VALUES ('Minifig 1', 'url1', 1, 1),
                       ('Minifig 2', 'url2', 2, 1)
            """.trimIndent())
            // Create a trigger called "updateNumOfMinifigsHiddenAndResetSomeStatesOfMinifig"
            // because it's not described in the schema for any past versions (1, 2, and 3) even
            // though it's present in the real database for said versions. I believe room database
            // migration testing relies mainly on the schemas, for example, to create databases.
            // Note that without this trigger a "SQLiteException: no such trigger" error will occur
            // because the migration to version 4 will try to drop it.
            execSQL("""
                CREATE TRIGGER updateNumOfMinifigsHiddenAndResetSomeStatesOfMinifig
                	AFTER UPDATE ON Minifigure
                	WHEN OLD.hidden != NEW.hidden
                		 AND NEW.hidden = 1
                BEGIN	
                	UPDATE Series
                	SET numOfMinifigsHidden = numOfMinifigsHidden + 1
                	WHERE id = NEW.seriesId;

                	UPDATE Minifigure
                	SET collected = 0,
                		wishListed = 0,
                		favorite = 0
                	WHERE id = NEW.id;
                END
            """.trimIndent())
            // Prepare for the next version
            close()
        }

        // Re-open the database with version 4, but first provide the migration1To4 as the migration process.
        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, TrackerForLegoMinifiguresSeriesDatabase.migration1To4)

        // MigrationTestHelper automatically verifies the schema changes, but not any trigger changes (at least not that I know of)
        // Make sure that the correct triggers exist after the migration, and that a certain deleted trigger
        // no longer exists (it was replaced by a new trigger since in sqlite triggers can't be updated)
        val newTriggers = setOf(
            "updateNumOfCollectedMinifigureInventoryItems",
            "updateCollectedValuesOfMinifigureInventoryItemsToFalse",
            "updateCollectedValuesOfMinifigureInventoryItemsToTrue",
            "updateNumOfMinifigsHiddenAndResetStatesOfMinifigAndCollectedValuesOfInventoryItems"
        )
        val deletedTrigger = "updateNumOfMinifigsHiddenAndResetSomeStatesOfMinifig"
        val presentTriggers = mutableSetOf<String>()
        // "use {}" ensures the cursor is closed properly, even if an exception is thrown. It's recommended to use it over "apply {}".
        db.query("SELECT name FROM sqlite_master WHERE type = 'trigger'").use { cursor ->
            while (cursor.moveToNext()) {
                presentTriggers.add(cursor.getString(0))
            }
        }
        // Check that all the new triggers are present
        assertTrue("There are missing new triggers", presentTriggers.containsAll(newTriggers))
        // Check that the deleted trigger is no longer present
        assertFalse("Old trigger 'deletedTrigger' should not exist", presentTriggers.contains(deletedTrigger))

        // Update the value of the new inventorySize column for each row in the Minifigure table
        // (in production the initial data load would take care of this)
        db.execSQL("""
            UPDATE Minifigure
            SET inventorySize = 2
        """.trimIndent())
        // Insert data into the MinifigureInventoryItem table for each minifigure (Again, in
        // production the initial data load would take care of this, but we're doing this here to
        // test the behavior of the new triggers)
        db.execSQL("""
            INSERT INTO MinifigureInventoryItem (name, imageUrl, partUrl, quantity, type, minifigureId)
            VALUES ('Item 1', 'url1', 'url1', 2, 'Part', 1),
                   ('Item 2', 'url2', 'url2', 1, 'Accessory', 1),
                   ('Item 3', 'url3', 'url3', 2, 'Part', 2),
                   ('Item 4', 'url4', 'url4', 1, 'Accessory', 2)
        """.trimIndent())
        // Test the updateCollectedValuesOfMinifigureInventoryItemsToTrue and
        // updateNumOfCollectedMinifigureInventoryItems triggers. For the latter trigger only the
        // case when "NEW.collected" is 1/true will be tested.
        //
        // Set the collected value of minifigure with id 1 to 1/true
        db.execSQL("""
            UPDATE Minifigure
            SET collected = 1
            WHERE id = 1
        """.trimIndent())
        // Make sure that all the inventory items of the corresponding minifigure have collected set
        // to 1/true
        db.query("SELECT collected FROM MinifigureInventoryItem WHERE minifigureId = 1").use { cursor ->
            assertEquals(true, cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
            assertEquals(true, cursor.moveToNext())
            assertEquals(1, cursor.getInt(0))
        }
        // Make sure that the numOfCollectedInventoryItems of the minifigure with id 1, is 2
        db.query("SELECT numOfCollectedInventoryItems FROM Minifigure WHERE id = 1").use { cursor ->
            assertEquals(true, cursor.moveToFirst())
            assertEquals(2, cursor.getInt(0))
        }
        // Test the updateNumOfMinifigsHiddenAndResetStatesOfMinifigAndCollectedValuesOfInventoryItems and
        // updateCollectedValuesOfMinifigureInventoryItemsToFalse triggers as well as the case when
        // "NEW.collected" is 0/false in the updateNumOfCollectedMinifigureInventoryItems trigger.
        //
        // Hide the minifigure with id 1
        db.execSQL("""
            UPDATE Minifigure
            SET hidden = 1
            WHERE id = 1
        """.trimIndent())
        // Make sure that the numOfMinifigsHidden for the only series is 1
        db.query("SELECT numOfMinifigsHidden FROM Series").use { cursor ->
            assertEquals(true, cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
        }
        // Make sure that the minifigure with id 1 is reset. This means that its collected,
        // wishListed, and favorite columns are set to 0/false, and that its
        // numOfCollectedInventoryItems column is set to 0.
        db.query("""
            SELECT * 
            FROM Minifigure
            WHERE id = 1
                AND collected = 0
                AND wishListed = 0
                AND favorite = 0
                AND numOfCollectedInventoryItems = 0
        """.trimIndent()).use { cursor ->
            assertTrue("Minifigure with id 1 should be reset", cursor.moveToFirst())
        }
        // Make sure that the corresponding inventory items of the hidden minifigure have collected
        // values of false
        db.query("""
            SELECT collected FROM MinifigureInventoryItem WHERE minifigureId = 1
        """.trimIndent()).use { cursor ->
            assertEquals(true, cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
            assertEquals(true, cursor.moveToNext())
            assertEquals(0, cursor.getInt(0))
        }
    }

//    @Test
//    @Throws(IOException::class)
//    fun testAllMigrations() {
//        // Create earliest version of the database.
//        helper.createDatabase(TEST_DB, 1).apply { close() }
//
//        // Open latest version of the database. Room validates the schema
//        // once all migrations execute.
//        Room.databaseBuilder(
//            InstrumentationRegistry.getInstrumentation().targetContext,
//            TrackerForLegoMinifiguresSeriesDatabase::class.java,
//            TEST_DB
//        ).addMigrations(*allMigrations).build().apply {
//            openHelper.writableDatabase.close()
//        }
//    }

}