package com.example.ruler.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ruler.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Query("SELECT * FROM trips ORDER BY start_date_epoch_millis ASC, title ASC")
    fun observeAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips ORDER BY start_date_epoch_millis ASC, title ASC")
    suspend fun getAllTrips(): List<TripEntity>

    @Query("SELECT * FROM trips WHERE trip_id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: String): TripEntity?

    @Query("SELECT COUNT(*) FROM trips WHERE title = :title")
    suspend fun countTripsByTitle(title: String): Int

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE trip_id = :tripId")
    suspend fun deleteTripById(tripId: String)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()
}
