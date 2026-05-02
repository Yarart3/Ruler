package com.example.ruler.data.repository

import com.example.ruler.data.fakeDB.FakeTripDataSource
import com.example.ruler.data.local.dao.ItineraryItemDao
import com.example.ruler.data.local.dao.TripDao
import com.example.ruler.domain.AuthRepository
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.TripRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val itineraryItemDao: ItineraryItemDao,
    private val authRepository: AuthRepository
) : TripRepository {
    override fun observeTrips(): Flow<List<Trip>> {
        val currentUserId = authRepository.currentUserId() ?: return flowOf(emptyList())
        return tripDao.observeTripsByOwner(currentUserId).map { trips ->
            trips.map { it.toDomain() }
        }
    }

    override suspend fun getTripById(id: String): Trip? {
        val currentUserId = authRepository.currentUserId() ?: return null
        return tripDao.getTripByIdForOwner(id, currentUserId)?.toDomain()
    }

    override suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip.toEntity(requireCurrentUserId()))
    }

    override suspend fun editTrip(trip: Trip) {
        tripDao.updateTrip(trip.toEntity(requireCurrentUserId()))
    }

    override suspend fun deleteTrip(id: String) {
        val currentTrip = getTripById(id) ?: return
        tripDao.deleteTrip(currentTrip.toEntity(requireCurrentUserId()))
    }

    override fun observeActivitiesByTrip(tripId: String): Flow<List<TripActivity>> {
        if (authRepository.currentUserId() == null) {
            return flowOf(emptyList())
        }
        return itineraryItemDao.observeItemsByTrip(tripId).map { items ->
            items.map { it.toDomain() }
        }
    }

    override suspend fun getActivitiesByTrip(tripId: String): List<TripActivity> {
        if (getTripById(tripId) == null) {
            return emptyList()
        }
        return itineraryItemDao.getItemsByTrip(tripId).map { it.toDomain() }
    }

    override suspend fun addActivity(activity: TripActivity) {
        if (getTripById(activity.tripId) == null) {
            throw IllegalArgumentException("Trip does not belong to the authenticated user")
        }
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
        val currentUserId = authRepository.currentUserId() ?: return
        claimLegacyTripsIfNeeded(currentUserId)
        if (tripDao.getTripsByOwner(currentUserId).isNotEmpty()) {
            return
        }

        tripDao.insertTrips(FakeTripDataSource.trips.map { it.toEntity(currentUserId) })

        if (FakeTripDataSource.activities.isNotEmpty()) {
            itineraryItemDao.insertItems(
                FakeTripDataSource.activities.mapIndexed { index, activity ->
                    activity.toEntity(displayOrder = index + 1)
                }
            )
        }
    }

    private suspend fun claimLegacyTripsIfNeeded(currentUserId: String) {
        if (tripDao.countTripsByOwner(currentUserId) > 0) {
            return
        }
        if (tripDao.countTripsByLegacyOwner(LEGACY_OWNER_USER_ID) == 0) {
            return
        }
        tripDao.reassignTripsToOwner(currentUserId, LEGACY_OWNER_USER_ID)
    }

    private fun requireCurrentUserId(): String {
        return authRepository.currentUserId() ?: error("No authenticated user")
    }

    companion object {
        private const val LEGACY_OWNER_USER_ID = "legacy_local_user"
    }
}
