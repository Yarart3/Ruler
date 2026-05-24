package com.example.ruler.domain

import kotlinx.coroutines.flow.Flow

interface TripImageRepository {
    fun getAllImages(): Flow<List<TripImage>>
    fun getImagesForTrip(tripId: String): Flow<List<TripImage>>
    suspend fun addImages(tripId: String, uris: List<String>)
    suspend fun deleteImage(imageId: String)
    suspend fun deleteAllForTrip(tripId: String)
}
