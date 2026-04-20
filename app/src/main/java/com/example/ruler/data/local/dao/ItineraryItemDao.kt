package com.example.ruler.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ruler.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItineraryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItineraryItemEntity>)

    @Query(
        """
        SELECT * FROM itinerary_items
        WHERE trip_owner_id = :tripId
        ORDER BY scheduled_at_epoch_millis ASC, display_order ASC, title ASC
        """
    )
    fun observeItemsByTrip(tripId: String): Flow<List<ItineraryItemEntity>>

    @Query(
        """
        SELECT * FROM itinerary_items
        WHERE trip_owner_id = :tripId
        ORDER BY scheduled_at_epoch_millis ASC, display_order ASC, title ASC
        """
    )
    suspend fun getItemsByTrip(tripId: String): List<ItineraryItemEntity>

    @Query("SELECT * FROM itinerary_items WHERE item_id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: String): ItineraryItemEntity?

    @Query("SELECT COUNT(*) FROM itinerary_items WHERE trip_owner_id = :tripId")
    suspend fun countItemsByTrip(tripId: String): Int

    @Update
    suspend fun updateItem(item: ItineraryItemEntity)

    @Delete
    suspend fun deleteItem(item: ItineraryItemEntity)

    @Query("DELETE FROM itinerary_items WHERE item_id = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("DELETE FROM itinerary_items WHERE trip_owner_id = :tripId")
    suspend fun deleteItemsByTripId(tripId: String)

    @Query("DELETE FROM itinerary_items")
    suspend fun deleteAllItems()
}
