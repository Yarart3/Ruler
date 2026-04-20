package com.example.ruler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ruler.data.local.dao.ItineraryItemDao
import com.example.ruler.data.local.dao.TripDao
import com.example.ruler.data.local.entity.ItineraryItemEntity
import com.example.ruler.data.local.entity.TripEntity

@Database(
    entities = [TripEntity::class, ItineraryItemEntity::class],
    version = 1,
    exportSchema = true
)
abstract class RulerDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    abstract fun itineraryItemDao(): ItineraryItemDao
}
