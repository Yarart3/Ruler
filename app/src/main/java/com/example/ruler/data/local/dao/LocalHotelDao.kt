package com.example.ruler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ruler.data.local.entity.LocalHotelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalHotelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotel(hotel: LocalHotelEntity)

    @Update
    suspend fun updateHotel(hotel: LocalHotelEntity)

    @Query("SELECT * FROM local_hotels WHERE hotel_id = :id LIMIT 1")
    suspend fun getHotelById(id: String): LocalHotelEntity?

    @Query("SELECT * FROM local_hotels WHERE owner_user_id = :userId ORDER BY name ASC")
    fun observeHotelsByOwner(userId: String): Flow<List<LocalHotelEntity>>

    @Query("DELETE FROM local_hotels WHERE hotel_id = :id")
    suspend fun deleteHotelById(id: String)
}
