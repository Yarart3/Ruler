package com.example.ruler.domain

import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrips(): Flow<List<Trip>>

    suspend fun getTripById(id: String): Trip?

    suspend fun addTrip(trip: Trip)

    suspend fun editTrip(trip: Trip)

    suspend fun deleteTrip(id: String)

    fun observeActivitiesByTrip(tripId: String): Flow<List<TripActivity>>

    suspend fun getActivitiesByTrip(tripId: String): List<TripActivity>

    suspend fun addActivity(activity: TripActivity)

    suspend fun updateActivity(activity: TripActivity)

    suspend fun deleteActivity(id: String)

    suspend fun seedInitialDataIfNeeded()
}
