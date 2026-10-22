package com.drsapps.trackerforlegominifiguresseries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drsapps.trackerforlegominifiguresseries.domain.model.Minifigure
import com.drsapps.trackerforlegominifiguresseries.domain.model.MinifigureInventoryItem
import com.drsapps.trackerforlegominifiguresseries.domain.model.Series

@Database(
    version = 4,
    entities = [Series::class, Minifigure::class, MinifigureInventoryItem::class],
    exportSchema = true
)
abstract class TrackerForLegoMinifiguresSeriesDatabase : RoomDatabase() {

    abstract fun getSeriesDao(): SeriesDao

    abstract fun getMinifigureDao(): MinifigureDao

    abstract fun getMinifigureInventoryItemDao(): MinifigureInventoryItemDao

    abstract fun getUpsertDao(): UpsertDao

    companion object {
        const val DATABASE_NAME = "tracker_for_lego_minifigures_series_db"

        // This function when called will update previous version schemas to the version 4 schema.
        private fun applyMigrationToVersion4Schema(db: SupportSQLiteDatabase) {
            // Add two new columns to the Minifigure table
            db.execSQL("""
                    ALTER TABLE Minifigure
                    ADD COLUMN numOfCollectedInventoryItems INTEGER NOT NULL DEFAULT 0
                """.trimIndent())
            db.execSQL("""                                        
                    ALTER TABLE Minifigure
                    ADD COLUMN inventorySize INTEGER NOT NULL DEFAULT -1
                """.trimIndent())

            // Create the MinifigureInventoryItem table
            db.execSQL("""
                    CREATE TABLE MinifigureInventoryItem (
                        id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        imageUrl TEXT NOT NULL,
                        partUrl TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        minifigureId INTEGER NOT NULL,
                        collected INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(id AUTOINCREMENT),
                        FOREIGN KEY(minifigureId) REFERENCES Minifigure(id) ON UPDATE CASCADE ON DELETE CASCADE
                    )
                """.trimIndent())

            // Create an index on the minifigureId column of the MinifigureInventoryTable.
            // This index was recommended by Android Studio so I added it in the Minifigure entity
            // definition. It has to do with efficiency for when the value that the foreign key references
            // changes. Since I added the index in the Minifigure entity definition I need to add it
            // in the migration too or else a
            // "java.lang.IllegalStateException: Migration didn't properly handle: MinifigureInventoryItem"
            // will occur. Which basically means that the schema after the migration doesn't match what's
            // expected.
            db.execSQL("CREATE INDEX index_MinifigureInventoryItem_minifigureId ON MinifigureInventoryItem(minifigureId)")

            // Update a trigger (drop the existing trigger and create the new updated trigger)
            db.execSQL("""
                    DROP TRIGGER updateNumOfMinifigsHiddenAndResetSomeStatesOfMinifig
                """.trimIndent())
            db.execSQL("""
                    CREATE TRIGGER updateNumOfMinifigsHiddenAndResetStatesOfMinifigAndCollectedValuesOfInventoryItems
                        AFTER UPDATE ON Minifigure
                        WHEN OLD.hidden != NEW.hidden
                            AND NEW.hidden = 1
                    BEGIN
                        UPDATE Series
                        SET numOfMinifigsHidden = numOfMinifigsHidden + 1
                        WHERE id = NEW.seriesId;
                        
                        UPDATE Minifigure
                        SET collected = 0,
                            wishlisted = 0,
                            favorite = 0
                        WHERE id = NEW.id;
                        
                        UPDATE MinifigureInventoryItem
                        SET collected = 0
                        WHERE minifigureId = NEW.id;
                    END
                """.trimIndent())

            // Create three new triggers
            // This trigger handles the case when a minifigure inventory item is collected or
            // uncollected
            db.execSQL("""
                CREATE TRIGGER updateNumOfCollectedMinifigureInventoryItems
                    AFTER UPDATE ON MinifigureInventoryItem
                    WHEN OLD.collected != NEW.collected
                BEGIN	
                    UPDATE Minifigure
                    SET numOfCollectedInventoryItems = 
                        CASE NEW.collected
                            WHEN 1 THEN numOfCollectedInventoryItems + 1
                            ELSE numOfCollectedInventoryItems - 1
                        END     
                    WHERE id = NEW.minifigureId;
                END
            """.trimIndent())
            // These two triggers handle the case when a minifigure is collected or uncollected
            db.execSQL("""
                CREATE TRIGGER updateCollectedValuesOfMinifigureInventoryItemsToFalse
                	AFTER UPDATE ON Minifigure
                	WHEN OLD.collected != NEW.collected
                		 AND NEW.collected = 0
                		 AND NEW.numOfCollectedInventoryItems == NEW.inventorySize
                BEGIN	
                	UPDATE MinifigureInventoryItem
                	SET collected = 0
                	WHERE minifigureId = NEW.id;
                END
            """.trimIndent())
            db.execSQL("""
                CREATE TRIGGER updateCollectedValuesOfMinifigureInventoryItemsToTrue
                	AFTER UPDATE ON Minifigure
                	WHEN OLD.collected != NEW.collected
                		 AND NEW.collected = 1
                BEGIN	
                	UPDATE MinifigureInventoryItem
                	SET collected = 1
                	WHERE minifigureId = NEW.id;
                END
            """.trimIndent())
        }

        val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    INSERT INTO Series (name, imageUrl, numOfMinifigs, releaseDate)
                    VALUES ('Dungeons & Dragons', '[REDACTED]', 12, '2024-09-01')
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO Minifigure (name, imageUrl, positionInSeries, seriesId)
                    VALUES
                        ('Dwarf Barbarian', '[REDACTED]', 1, 46),
                        ('Gith Warlock', '[REDACTED]', 2, 46),
                        ('Tiefling Sorcerer', '[REDACTED]', 3, 46),
                        ('Dragonborn Paladin', '[REDACTED]', 4, 46),
                        ('Halfling Druid', '[REDACTED]', 5, 46),
                        ('Aarakocra Ranger', '[REDACTED]', 6, 46),
                        ('Mind Flayer', '[REDACTED]', 7, 46),
                        ('Strahd von Zarovich', '[REDACTED]', 8, 46),
                        ('Elf Bard', '[REDACTED]', 9, 46),
                        ('The Lady of Pain', '[REDACTED]', 10, 46),
                        ('Szass Tam', '[REDACTED]', 11, 46),
                        ('Tasha the Witch Queen', '[REDACTED]', 12, 46)
                """.trimIndent())
            }
        }

        val migration2To3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    INSERT INTO Series (name, imageUrl, numOfMinifigs, releaseDate)
                    VALUES ('Series 27', '[REDACTED]', 12, '2025-01-01')
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO Minifigure (name, imageUrl, positionInSeries, seriesId)
                    VALUES
                        ('Hamster Costume Fan', '[REDACTED]', 1, 47),
                        ('Wolfpack Beastmaster', '[REDACTED]', 2, 47),
                        ('Jetpack Racer', '[REDACTED]', 3, 47),
                        ('Astronomer Kid', '[REDACTED]', 4, 47),
                        ('Plush Toy Collector', '[REDACTED]', 5, 47),
                        ('Pterodactyl Costume Fan', '[REDACTED]', 6, 47),
                        ('Longboarder', '[REDACTED]', 7, 47),
                        ('Bogeyman', '[REDACTED]', 8, 47),
                        ('Cupid', '[REDACTED]', 9, 47),
                        ('Pirate Quartermaster', '[REDACTED]', 10, 47),
                        ('Cat Lover', '[REDACTED]', 11, 47),
                        ('Steampunk Inventor', '[REDACTED]', 12, 47)
                """.trimIndent())
            }
        }

        val migration1To4 = object : Migration(1, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                applyMigrationToVersion4Schema(db)
            }
        }

        val migration2to4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                applyMigrationToVersion4Schema(db)
            }
        }

        val migration3to4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                applyMigrationToVersion4Schema(db)
            }
        }
    }

}