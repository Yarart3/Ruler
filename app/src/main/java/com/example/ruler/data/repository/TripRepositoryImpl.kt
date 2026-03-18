package com.example.ruler.data.repository

import com.example.ruler.data.fakeDB.FakeTripDataSource
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.TripRepository

class TripRepositoryImpl : TripRepository {
    override fun getTrips(): List<Trip> {
        return FakeTripDataSource.trips.toList()
    }

    override fun getTripById(id: String): Trip? {
        return FakeTripDataSource.trips.find { it.id == id }
    }

    override fun addTrip(trip: Trip) {
        FakeTripDataSource.trips.add(trip)
    }

    override fun editTrip(trip: Trip) {
        val index = FakeTripDataSource.trips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            FakeTripDataSource.trips[index] = trip
        }
    }

    override fun deleteTrip(id: String) {
        FakeTripDataSource.trips.removeAll { it.id == id }
        FakeTripDataSource.activities.removeAll { it.tripId == id }
    }

    override fun getActivitiesByTrip(tripId: String): List<TripActivity> {
        return FakeTripDataSource.activities.filter { it.tripId == tripId }
    }

    override fun addActivity(activity: TripActivity) {
        FakeTripDataSource.activities.add(activity)
    }

    override fun updateActivity(activity: TripActivity) {
        val index = FakeTripDataSource.activities.indexOfFirst { it.id == activity.id }
        if (index != -1) {
            FakeTripDataSource.activities[index] = activity
        }
    }

    override fun deleteActivity(id: String) {
        FakeTripDataSource.activities.removeAll { it.id == id }
    }
}
