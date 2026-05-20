package com.example.ruler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ruler.data.local.dao.AccessLogDao
import com.example.ruler.data.local.dao.ItineraryItemDao
import com.example.ruler.data.local.dao.TripDao
import com.example.ruler.data.local.dao.UserDao
import com.example.ruler.data.local.entity.AccessLogEntity
import com.example.ruler.data.local.entity.ItineraryItemEntity
import com.example.ruler.data.local.entity.TripEntity
import com.example.ruler.data.local.entity.UserEntity

@Database(
    entities = [TripEntity::class, ItineraryItemEntity::class, UserEntity::class, AccessLogEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class RulerDatabase : RoomDatabase() {
    abstract fun accessLogDao(): AccessLogDao

    abstract fun tripDao(): TripDao

    abstract fun itineraryItemDao(): ItineraryItemDao

    abstract fun userDao(): UserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `users` (
                        `user_id` TEXT NOT NULL,
                        `login_email` TEXT NOT NULL,
                        `username` TEXT NOT NULL,
                        `birth_date` INTEGER,
                        `address` TEXT NOT NULL,
                        `country` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `accepts_marketing_emails` INTEGER NOT NULL,
                        PRIMARY KEY(`user_id`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)"
                )
                database.execSQL(
                    "ALTER TABLE `trips` ADD COLUMN `owner_user_id` TEXT NOT NULL DEFAULT 'legacy_local_user'"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_trips_owner_user_id` ON `trips` (`owner_user_id`)"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `access_logs` (
                        `log_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `user_id` TEXT NOT NULL,
                        `event_type` TEXT NOT NULL,
                        `occurred_at_epoch_millis` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_access_logs_user_id` ON `access_logs` (`user_id`)"
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_access_logs_occurred_at_epoch_millis`
                    ON `access_logs` (`occurred_at_epoch_millis`)
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_reservation_id` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_id` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_name` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_address` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_image_url` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_room_id` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_room_type` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_room_price_per_night` REAL")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_room_image_urls` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_guest_name` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_guest_email` TEXT")
                database.execSQL("ALTER TABLE `trips` ADD COLUMN `hotel_reservation_nights` INTEGER")
            }
        }
    }
}
