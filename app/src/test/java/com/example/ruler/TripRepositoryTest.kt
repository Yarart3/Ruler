package com.example.ruler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.domain.HotelReservationDetails
import com.example.ruler.domain.Trip
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TripRepositoryTest {

    private lateinit var database: RulerDatabase
    private lateinit var repository: TripRepositoryImpl
    private val authRepository = FakeAuthRepository(
        userId = "user-1",
        email = "user1@example.com",
        username = "user1"
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TripRepositoryImpl(database.tripDao(), database.itineraryItemDao(), authRepository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addTrip_addsTripToObservedTrips() = runBlocking {
        val trip = createTrip(id = "trip-1")

        repository.addTrip(trip)

        val trips = repository.observeTrips().first()
        assertTrue(trips.contains(trip))
    }

    @Test
    fun editTrip_updatesTripData() = runBlocking {
        val originalTrip = createTrip(id = "trip-1", title = "Original")
        val editedTrip = originalTrip.copy(title = "Edited", destination = "Tokyo, Japan")
        repository.addTrip(originalTrip)

        repository.editTrip(editedTrip)

        val storedTrip = repository.getTripById("trip-1")
        assertNotNull(storedTrip)
        assertEquals("Edited", storedTrip?.title)
        assertEquals("Tokyo, Japan", storedTrip?.destination)
    }

    @Test
    fun deleteTrip_removesTripFromObservedTrips() = runBlocking {
        val trip = createTrip(id = "trip-1")
        repository.addTrip(trip)

        repository.deleteTrip(trip.id)

        val trips = repository.observeTrips().first()
        assertTrue(trips.none { it.id == trip.id })
    }

    @Test
    fun getTripById_withValidId_returnsTrip() = runBlocking {
        val trip = createTrip(id = "trip-1", title = "Barcelona")
        repository.addTrip(trip)

        val result = repository.getTripById("trip-1")

        assertNotNull(result)
        assertEquals("Barcelona", result?.title)
    }

    @Test
    fun addTrip_persistsHotelReservationDetails() = runBlocking {
        val trip = createTrip(id = "trip-hotel").copy(
            hotelReservation = HotelReservationDetails(
                reservationId = "reservation-1",
                hotelId = "PAR01",
                hotelName = "Hotel Louvre",
                hotelAddress = "Rue de Rivoli 99, Paris",
                hotelImageUrl = "http://15.224.84.148:8090/images/PAR01.png",
                roomId = "R2",
                roomType = "double",
                roomPricePerNight = 120.0,
                roomImageUrls = listOf(
                    "http://15.224.84.148:8090/images/PAR01R2.png"
                ),
                guestName = "Ada Lovelace",
                guestEmail = "ada@example.com",
                nights = 4
            )
        )

        repository.addTrip(trip)

        val storedTrip = repository.getTripById("trip-hotel")
        assertNotNull(storedTrip?.hotelReservation)
        assertEquals("reservation-1", storedTrip?.hotelReservation?.reservationId)
        assertEquals("Hotel Louvre", storedTrip?.hotelReservation?.hotelName)
        assertEquals(120.0, storedTrip?.hotelReservation?.roomPricePerNight ?: 0.0, 0.0)
        assertEquals(1, storedTrip?.hotelReservation?.roomImageUrls?.size)
    }

    @Test
    fun getTripById_withUnknownId_returnsNull() = runBlocking {
        val result = repository.getTripById("missing-id")

        assertNull(result)
    }

    @Test
    fun observeTrips_filtersTripsByAuthenticatedUser() = runBlocking {
        val otherUserRepository = TripRepositoryImpl(
            database.tripDao(),
            database.itineraryItemDao(),
            FakeAuthRepository(userId = "user-2", email = "user2@example.com", username = "user2")
        )

        repository.addTrip(createTrip(id = "trip-1", title = "Mine"))
        otherUserRepository.addTrip(createTrip(id = "trip-2", title = "Other"))

        val trips = repository.observeTrips().first()

        assertEquals(1, trips.size)
        assertEquals("Mine", trips.single().title)
    }

    @Test
    fun seedInitialDataIfNeeded_doesNotCreateDefaultTripsForNewUsers() = runBlocking {
        repository.seedInitialDataIfNeeded()

        val trips = repository.observeTrips().first()

        assertTrue(trips.isEmpty())
    }

    @Test
    fun seedInitialDataIfNeeded_keepsLegacyTripsHiddenForNewUsers() = runBlocking {
        database.tripDao().insertTrip(
            com.example.ruler.data.local.entity.TripEntity(
                id = "legacy-trip",
                title = "Legacy",
                destination = "Lleida, Spain",
                ownerUserId = "legacy_local_user",
                startDateEpochMillis = 1_000L,
                endDateEpochMillis = 2_000L,
                description = "Legacy trip",
                budgetAmount = 100,
                budgetCurrency = "EUR",
                emoji = "🧭"
            )
        )

        repository.seedInitialDataIfNeeded()

        val trips = repository.observeTrips().first()

        assertTrue(trips.isEmpty())
    }

    private fun createTrip(
        id: String,
        title: String = "Trip",
        destination: String = "Valencia, Spain"
    ) = Trip(
        id = id,
        title = title,
        destination = destination,
        startDate = "10/04/2026",
        endDate = "15/04/2026",
        description = "Test trip",
        budget = "300",
        emoji = "✈️"
    )
}
