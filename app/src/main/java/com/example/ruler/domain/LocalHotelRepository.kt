package com.example.ruler.domain

import kotlinx.coroutines.flow.Flow

interface LocalHotelRepository {
    fun observeHotels(): Flow<List<LocalHotel>>
    suspend fun getHotelById(id: String): LocalHotel?
    suspend fun addHotel(hotel: LocalHotel)
    suspend fun updateHotel(hotel: LocalHotel)
    suspend fun deleteHotel(id: String)
}
