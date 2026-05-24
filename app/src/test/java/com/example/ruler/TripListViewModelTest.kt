package com.example.ruler

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.LocalHotelRepository
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.TripRepository
import com.example.ruler.ui.viewmodels.TripListViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TripListViewModelTest {

    @Test
    fun assignHotelToTrip_persistsReservationDetailsWhenHotelHasReservationData() {
        val trip = Trip(
            id = "trip-1",
            title = "Paris",
            destination = "Paris",
            startDate = "10/06/2026",
            endDate = "15/06/2026",
            description = "Trip desc",
            budget = "100",
            emoji = "✈️"
        )
        val hotel = LocalHotel(
            id = "hotel-1",
            name = "Hotel Test",
            address = "Main Street",
            nights = 3,
            pricePerNight = 120.0,
            reservationId = "res-1",
            remoteHotelId = "remote-hotel-1",
            remoteRoomId = "remote-room-1",
            startDate = "2026-06-10",
            endDate = "2026-06-13",
            guestName = "Ada",
            guestEmail = "ada@example.com",
            hotelImageUrl = "file://hotel.jpg",
            roomImageUrls = listOf("file://room1.jpg")
        )

        val tripRepository = FakeTripRepositoryForTripList(trip)
        val localHotelRepository = FakeLocalHotelRepositoryForTripList(hotel)
        val viewModel = TripListViewModel(
            repository = tripRepository,
            localHotelRepository = localHotelRepository,
            context = ApplicationProvider.getApplicationContext()
        )

        viewModel.assignHotelToTrip(
            tripId = "trip-1",
            hotelId = "hotel-1",
            hotelName = "Hotel Test",
            hotelAddress = "Main Street",
            checkIn = "10/06/2026",
            checkOut = "13/06/2026"
        )
        shadowOf(Looper.getMainLooper()).idle()

        val updatedTrip = tripRepository.trips.value.single()
        assertEquals("hotel-1", updatedTrip.localHotels.single().hotelId)
        assertNotNull(updatedTrip.hotelReservation)
        assertEquals("res-1", updatedTrip.hotelReservation?.reservationId)
        assertEquals("remote-room-1", updatedTrip.hotelReservation?.roomId)
        assertEquals("Ada", updatedTrip.hotelReservation?.guestName)
        assertEquals("trip-1", localHotelRepository.hotels.value.single().assignedTripId)
    }

    @Test
    fun assignHotelToTrip_keepsReservationNullWhenHotelIsOnlyLocalAssignment() {
        val trip = Trip(
            id = "trip-1",
            title = "Paris",
            destination = "Paris",
            startDate = "10/06/2026",
            endDate = "15/06/2026",
            description = "Trip desc",
            budget = "100",
            emoji = "✈️"
        )
        val hotel = LocalHotel(
            id = "hotel-1",
            name = "Hotel Test",
            address = "Main Street"
        )

        val tripRepository = FakeTripRepositoryForTripList(trip)
        val localHotelRepository = FakeLocalHotelRepositoryForTripList(hotel)
        val viewModel = TripListViewModel(
            repository = tripRepository,
            localHotelRepository = localHotelRepository,
            context = ApplicationProvider.getApplicationContext()
        )

        viewModel.assignHotelToTrip(
            tripId = "trip-1",
            hotelId = "hotel-1",
            hotelName = "Hotel Test",
            hotelAddress = "Main Street",
            checkIn = "10/06/2026",
            checkOut = "13/06/2026"
        )
        shadowOf(Looper.getMainLooper()).idle()

        val updatedTrip = tripRepository.trips.value.single()
        assertEquals("hotel-1", updatedTrip.localHotels.single().hotelId)
        assertNull(updatedTrip.hotelReservation)
    }
}

private class FakeLocalHotelRepositoryForTripList(
    hotel: LocalHotel? = null
) : LocalHotelRepository {

    val hotels = MutableStateFlow(hotel?.let(::listOf) ?: emptyList())

    override fun observeHotels(): Flow<List<LocalHotel>> = hotels

    override suspend fun getHotelById(id: String): LocalHotel? =
        hotels.value.firstOrNull { it.id == id }

    override suspend fun addHotel(hotel: LocalHotel) {
        hotels.value = hotels.value + hotel
    }

    override suspend fun updateHotel(hotel: LocalHotel) {
        hotels.value = hotels.value.map {
            if (it.id == hotel.id) hotel else it
        }
    }

    override suspend fun deleteHotel(id: String) {
        hotels.value = hotels.value.filterNot { it.id == id }
    }
}

private class FakeTripRepositoryForTripList(
    initialTrip: Trip
) : TripRepository {

    val trips = MutableStateFlow(listOf(initialTrip))

    override fun observeTrips(): Flow<List<Trip>> = trips

    override suspend fun getTripById(id: String): Trip? = trips.value.firstOrNull { it.id == id }

    override suspend fun addTrip(trip: Trip) {
        trips.value = trips.value + trip
    }

    override suspend fun editTrip(trip: Trip) {
        trips.value = trips.value.map {
            if (it.id == trip.id) trip else it
        }
    }

    override suspend fun deleteTrip(id: String) {
        trips.value = trips.value.filterNot { it.id == id }
    }

    override fun observeActivitiesByTrip(tripId: String): Flow<List<TripActivity>> =
        MutableStateFlow(emptyList())

    override suspend fun getActivitiesByTrip(tripId: String): List<TripActivity> = emptyList()

    override suspend fun addActivity(activity: TripActivity) = Unit

    override suspend fun updateActivity(activity: TripActivity) = Unit

    override suspend fun deleteActivity(id: String) = Unit

    override suspend fun seedInitialDataIfNeeded() = Unit
}
