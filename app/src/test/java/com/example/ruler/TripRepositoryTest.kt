package com.example.ruler

import com.example.ruler.data.fakeDB.FakeTripDataSource
import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.domain.Trip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TripRepositoryTest {

    private lateinit var repository: TripRepositoryImpl

    @Before
    fun setUp() {
        FakeTripDataSource.trips.clear()
        FakeTripDataSource.activities.clear()
        repository = TripRepositoryImpl()
    }

    @Test
    fun addTrip_addsTripToGetTrips() {
        val trip = createTrip(id = "trip-1")

        repository.addTrip(trip)

        val trips = repository.getTrips()
        assertTrue(trips.contains(trip))
    }

    @Test
    fun editTrip_updatesTripData() {
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
    fun deleteTrip_removesTripFromGetTrips() {
        val trip = createTrip(id = "trip-1")
        repository.addTrip(trip)

        repository.deleteTrip(trip.id)

        val trips = repository.getTrips()
        assertTrue(trips.none { it.id == trip.id })
    }

    @Test
    fun getTripById_withValidId_returnsTrip() {
        val trip = createTrip(id = "trip-1", title = "Barcelona")
        repository.addTrip(trip)

        val result = repository.getTripById("trip-1")

        assertNotNull(result)
        assertEquals("Barcelona", result?.title)
    }

    @Test
    fun getTripById_withUnknownId_returnsNull() {
        val result = repository.getTripById("missing-id")

        assertNull(result)
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
