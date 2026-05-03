package com.example.ruler.data.repository

import android.util.Log
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
        val currentUserId = authRepository.currentUserId() ?: run {
            Log.w(TAG, "observeTrips: no hay usuario autenticado")
            return flowOf(emptyList())
        }
        Log.d(TAG, "observeTrips: observando viajes de userId=$currentUserId")
        return tripDao.observeTripsByOwner(currentUserId).map { trips ->
            Log.d(TAG, "observeTrips: emitiendo ${trips.size} viajes")
            trips.map { it.toDomain() }
        }
    }

    override suspend fun getTripById(id: String): Trip? {
        val currentUserId = authRepository.currentUserId() ?: run {
            Log.w(TAG, "getTripById: no hay usuario autenticado")
            return null
        }
        val trip = tripDao.getTripByIdForOwner(id, currentUserId)?.toDomain()
        if (trip == null) Log.w(TAG, "getTripById: viaje no encontrado id=$id")
        else Log.d(TAG, "getTripById: viaje encontrado id=$id title='${trip.title}'")
        return trip
    }

    override suspend fun addTrip(trip: Trip) {
        Log.i(TAG, "addTrip: insertando viaje id=${trip.id} title='${trip.title}'")
        tripDao.insertTrip(trip.toEntity(requireCurrentUserId()))
        Log.i(TAG, "addTrip: viaje insertado correctamente id=${trip.id}")
    }

    override suspend fun editTrip(trip: Trip) {
        Log.i(TAG, "editTrip: actualizando viaje id=${trip.id} title='${trip.title}'")
        tripDao.updateTrip(trip.toEntity(requireCurrentUserId()))
        Log.i(TAG, "editTrip: viaje actualizado correctamente id=${trip.id}")
    }

    override suspend fun deleteTrip(id: String) {
        Log.i(TAG, "deleteTrip: eliminando viaje id=$id")
        val currentTrip = getTripById(id) ?: run {
            Log.w(TAG, "deleteTrip: viaje no encontrado id=$id")
            return
        }
        tripDao.deleteTrip(currentTrip.toEntity(requireCurrentUserId()))
        Log.i(TAG, "deleteTrip: viaje eliminado correctamente id=$id")
    }

    override fun observeActivitiesByTrip(tripId: String): Flow<List<TripActivity>> {
        if (authRepository.currentUserId() == null) {
            Log.w(TAG, "observeActivitiesByTrip: no hay usuario autenticado")
            return flowOf(emptyList())
        }
        Log.d(TAG, "observeActivitiesByTrip: observando actividades de tripId=$tripId")
        return itineraryItemDao.observeItemsByTrip(tripId).map { items ->
            Log.d(TAG, "observeActivitiesByTrip: emitiendo ${items.size} actividades para tripId=$tripId")
            items.map { it.toDomain() }
        }
    }

    override suspend fun getActivitiesByTrip(tripId: String): List<TripActivity> {
        if (getTripById(tripId) == null) {
            Log.w(TAG, "getActivitiesByTrip: viaje no encontrado tripId=$tripId")
            return emptyList()
        }
        val items = itineraryItemDao.getItemsByTrip(tripId).map { it.toDomain() }
        Log.d(TAG, "getActivitiesByTrip: ${items.size} actividades para tripId=$tripId")
        return items
    }

    override suspend fun addActivity(activity: TripActivity) {
        Log.i(TAG, "addActivity: insertando actividad id=${activity.id} title='${activity.title}' tripId=${activity.tripId}")
        if (getTripById(activity.tripId) == null) {
            Log.e(TAG, "addActivity: el viaje no pertenece al usuario autenticado tripId=${activity.tripId}")
            throw IllegalArgumentException("Trip does not belong to the authenticated user")
        }
        val nextDisplayOrder = itineraryItemDao.countItemsByTrip(activity.tripId) + 1
        itineraryItemDao.insertItem(activity.toEntity(displayOrder = nextDisplayOrder))
        Log.i(TAG, "addActivity: actividad insertada correctamente id=${activity.id}")
    }

    override suspend fun updateActivity(activity: TripActivity) {
        Log.i(TAG, "updateActivity: actualizando actividad id=${activity.id} title='${activity.title}'")
        val existingItem = itineraryItemDao.getItemById(activity.id)
        itineraryItemDao.updateItem(activity.toEntity(existingItem))
        Log.i(TAG, "updateActivity: actividad actualizada correctamente id=${activity.id}")
    }

    override suspend fun deleteActivity(id: String) {
        Log.i(TAG, "deleteActivity: eliminando actividad id=$id")
        itineraryItemDao.deleteItemById(id)
        Log.i(TAG, "deleteActivity: actividad eliminada correctamente id=$id")
    }

    override suspend fun seedInitialDataIfNeeded() {
        val currentUserId = authRepository.currentUserId() ?: run {
            Log.w(TAG, "seedInitialDataIfNeeded: no hay usuario autenticado")
            return
        }
        claimLegacyTripsIfNeeded(currentUserId)
        if (tripDao.getTripsByOwner(currentUserId).isNotEmpty()) {
            Log.d(TAG, "seedInitialDataIfNeeded: el usuario ya tiene datos, omitiendo seed")
            return
        }
        Log.i(TAG, "seedInitialDataIfNeeded: insertando datos iniciales para userId=$currentUserId")
        tripDao.insertTrips(FakeTripDataSource.trips.map { it.toEntity(currentUserId) })
        if (FakeTripDataSource.activities.isNotEmpty()) {
            itineraryItemDao.insertItems(
                FakeTripDataSource.activities.mapIndexed { index, activity ->
                    activity.toEntity(displayOrder = index + 1)
                }
            )
        }
        Log.i(TAG, "seedInitialDataIfNeeded: datos iniciales insertados correctamente")
    }

    private suspend fun claimLegacyTripsIfNeeded(currentUserId: String) {
        if (tripDao.countTripsByOwner(currentUserId) > 0) {
            Log.d(TAG, "claimLegacyTrips: el usuario ya tiene viajes propios, omitiendo")
            return
        }
        if (tripDao.countTripsByLegacyOwner(LEGACY_OWNER_USER_ID) == 0) {
            Log.d(TAG, "claimLegacyTrips: no hay viajes legacy que reclamar")
            return
        }
        Log.i(TAG, "claimLegacyTrips: reasignando viajes legacy a userId=$currentUserId")
        tripDao.reassignTripsToOwner(currentUserId, LEGACY_OWNER_USER_ID)
        Log.i(TAG, "claimLegacyTrips: viajes reasignados correctamente")
    }

    private fun requireCurrentUserId(): String {
        return authRepository.currentUserId() ?: run {
            Log.e(TAG, "requireCurrentUserId: no hay usuario autenticado")
            error("No authenticated user")
        }
    }

    companion object {
        private const val TAG = "TripRepositoryImpl"
        private const val LEGACY_OWNER_USER_ID = "legacy_local_user"
    }
}