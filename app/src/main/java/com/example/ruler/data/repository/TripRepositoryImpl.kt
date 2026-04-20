package com.example.ruler.data.repository

import com.example.ruler.data.fakeDB.FakeTripDataSource
import com.example.ruler.data.local.dao.ItineraryItemDao
import com.example.ruler.data.local.dao.TripDao
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.TripRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val itineraryItemDao: ItineraryItemDao
) : TripRepository {
    override fun observeTrips(): Flow<List<Trip>> {
        return tripDao.observeAllTrips().map { trips ->
            trips.map { it.toDomain() }
        }
    }

    override suspend fun getTripById(id: String): Trip? {
        return tripDao.getTripById(id)?.toDomain()
    }

    override suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip.toEntity())
    }

    override suspend fun editTrip(trip: Trip) {
        tripDao.updateTrip(trip.toEntity())
    }

    override suspend fun deleteTrip(id: String) {
        tripDao.deleteTripById(id)
    }

    override fun observeActivitiesByTrip(tripId: String): Flow<List<TripActivity>> {
        return itineraryItemDao.observeItemsByTrip(tripId).map { items ->
            items.map { it.toDomain() }
        }
    }

    override suspend fun getActivitiesByTrip(tripId: String): List<TripActivity> {
        return itineraryItemDao.getItemsByTrip(tripId).map { it.toDomain() }
    }

    override suspend fun addActivity(activity: TripActivity) {
        val nextDisplayOrder = itineraryItemDao.countItemsByTrip(activity.tripId) + 1
        itineraryItemDao.insertItem(activity.toEntity(displayOrder = nextDisplayOrder))
    }

    override suspend fun updateActivity(activity: TripActivity) {
        val existingItem = itineraryItemDao.getItemById(activity.id)
        itineraryItemDao.updateItem(activity.toEntity(existingItem))
    }

    override suspend fun deleteActivity(id: String) {
        itineraryItemDao.deleteItemById(id)
    }

    override suspend fun seedInitialDataIfNeeded() {
        if (tripDao.getAllTrips().isNotEmpty()) {
            return
        }

        tripDao.insertTrips(FakeTripDataSource.trips.map { it.toEntity() })

        if (FakeTripDataSource.activities.isNotEmpty()) {
            itineraryItemDao.insertItems(
                FakeTripDataSource.activities.mapIndexed { index, activity ->
                    activity.toEntity(displayOrder = index + 1)
                }
            )
        }
    }
}
