package com.example.ruler

import android.os.Looper
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelAvailability
import com.example.ruler.domain.HotelRepository
import com.example.ruler.domain.HotelReservation
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelReservationResult
import com.example.ruler.domain.HotelRoom
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.LocalHotelRepository
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.TripRepository
import com.example.ruler.ui.viewmodels.LocalHotelViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LocalHotelViewModelTest {

    @Test
    fun deleteHotel_removesReservationLocallyWhenRemoteCancellationSucceeds() {
        val localHotel = LocalHotel(
            id = "hotel-1",
            name = "Hotel Test",
            address = "Main Street",
            assignedTripId = "trip-1",
            reservationId = "res-1"
        )
        val localRepository = FakeLocalHotelRepository(localHotel)
        val tripRepository = FakeTripRepository(
            Trip(
                id = "trip-1",
                title = "Trip",
                destination = "Paris",
                startDate = "10/06/2026",
                endDate = "12/06/2026",
                description = "desc",
                budget = "100",
                emoji = "✈️",
                localHotels = listOf(
                    com.example.ruler.domain.LocalHotelAssignment(
                        hotelId = "hotel-1",
                        hotelName = "Hotel Test",
                        hotelAddress = "Main Street",
                        checkInDate = "10/06/2026",
                        checkOutDate = "12/06/2026"
                    )
                )
            )
        )
        val hotelRepository = FakeHotelRepository()
        val viewModel = LocalHotelViewModel(localRepository, hotelRepository, tripRepository)

        viewModel.deleteHotel("hotel-1")
        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(localRepository.deletedIds.contains("hotel-1"))
        assertEquals(listOf("res-1"), hotelRepository.cancelledReservationIds)
        assertTrue(tripRepository.trips.value.single().localHotels.isEmpty())
        assertEquals("Reservation cancelled", viewModel.uiState.value.successMessage)
    }

    @Test
    fun deleteHotel_keepsReservationLocallyWhenRemoteCancellationFails() {
        val localHotel = LocalHotel(
            id = "hotel-1",
            name = "Hotel Test",
            address = "Main Street",
            reservationId = "res-1"
        )
        val localRepository = FakeLocalHotelRepository(localHotel)
        val tripRepository = FakeTripRepository()
        val hotelRepository = FakeHotelRepository(cancelByIdResult = Result.failure(IllegalStateException("API error")))
        val viewModel = LocalHotelViewModel(localRepository, hotelRepository, tripRepository)

        viewModel.deleteHotel("hotel-1")
        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(localRepository.deletedIds.isEmpty())
        assertEquals("API error", viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.successMessage)
    }
}

private class FakeLocalHotelRepository(
    hotel: LocalHotel? = null
) : LocalHotelRepository {

    private val hotels = MutableStateFlow(hotel?.let(::listOf) ?: emptyList())
    val deletedIds = mutableListOf<String>()

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
        deletedIds += id
        hotels.value = hotels.value.filterNot { it.id == id }
    }
}

private class FakeHotelRepository(
    private val cancelByIdResult: Result<Unit> = Result.success(Unit),
    private val cancelResult: Result<Unit> = Result.success(Unit)
) : HotelRepository {

    val cancelledReservationIds = mutableListOf<String>()

    override suspend fun listHotels(): Result<List<Hotel>> = Result.success(emptyList())

    override suspend fun checkAvailability(
        startDate: String,
        endDate: String,
        hotelId: String?,
        city: String?
    ): Result<HotelAvailability> = Result.success(HotelAvailability(emptyList()))

    override suspend fun reserveRoom(request: HotelReservationRequest): Result<HotelReservationResult> =
        Result.failure(UnsupportedOperationException())

    override suspend fun cancelReservation(request: HotelReservationRequest): Result<Unit> = cancelResult

    override suspend fun cancelReservationById(reservationId: String): Result<Unit> {
        cancelledReservationIds += reservationId
        return cancelByIdResult
    }

    override suspend fun listReservations(guestEmail: String?): Result<List<HotelReservation>> =
        Result.success(emptyList())
}

private class FakeTripRepository(
    initialTrip: Trip? = null
) : TripRepository {

    val trips = MutableStateFlow(initialTrip?.let(::listOf) ?: emptyList())

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
