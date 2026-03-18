package com.example.ruler.domain

interface TripRepository {
    fun getTrips(): List<Trip>

    fun getTripById(id: String): Trip?

    fun addTrip(trip: Trip)

    fun editTrip(trip: Trip)

    fun deleteTrip(id: String)

    fun getActivitiesByTrip(tripId: String): List<TripActivity>

    fun addActivity(activity: TripActivity)

    fun updateActivity(activity: TripActivity)

    fun deleteActivity(id: String)
}
