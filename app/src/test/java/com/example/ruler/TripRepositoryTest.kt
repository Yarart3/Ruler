package com.example.ruler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.repository.TripRepositoryImpl
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

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TripRepositoryImpl(database.tripDao(), database.itineraryItemDao())
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
    fun getTripById_withUnknownId_returnsNull() = runBlocking {
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
