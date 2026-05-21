package com.example.ruler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ruler.data.local.entity.TripImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripImageDao {

    @Query("SELECT * FROM trip_images WHERE trip_id = :tripId ORDER BY added_at ASC")
    fun getImagesForTrip(tripId: String): Flow<List<TripImageEntity>>

    @Query("SELECT * FROM trip_images WHERE image_id = :imageId LIMIT 1")
    suspend fun getImageById(imageId: String): TripImageEntity?

    @Query("SELECT * FROM trip_images WHERE trip_id = :tripId ORDER BY added_at ASC")
    suspend fun getImagesForTripSnapshot(tripId: String): List<TripImageEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImages(images: List<TripImageEntity>)

    @Query("DELETE FROM trip_images WHERE image_id = :imageId")
    suspend fun deleteImage(imageId: String)

    @Query("DELETE FROM trip_images WHERE trip_id = :tripId")
    suspend fun deleteAllForTrip(tripId: String)
}
