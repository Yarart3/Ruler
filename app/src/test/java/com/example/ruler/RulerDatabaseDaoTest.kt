package com.example.ruler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.local.entity.AccessLogEntity
import com.example.ruler.data.local.entity.ItineraryItemEntity
import com.example.ruler.data.local.entity.TripEntity
import com.example.ruler.data.local.entity.UserEntity
import java.util.Date
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RulerDatabaseDaoTest {

    private lateinit var database: RulerDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun tripDao_observeTripsByOwner_filtersAndOrdersTrips() = runBlocking {
        database.tripDao().insertTrips(
            listOf(
                tripEntity(
                    id = "trip-b",
                    ownerUserId = "user-1",
                    title = "B trip",
                    startDateEpochMillis = 2_000L
                ),
                tripEntity(
                    id = "trip-a",
                    ownerUserId = "user-1",
                    title = "A trip",
                    startDateEpochMillis = 1_000L
                ),
                tripEntity(
                    id = "trip-other",
                    ownerUserId = "user-2",
                    title = "Other user's trip",
                    startDateEpochMillis = 500L
                )
            )
        )

        val trips = database.tripDao().observeTripsByOwner("user-1").first()

        assertEquals(listOf("trip-a", "trip-b"), trips.map { it.id })
    }

    @Test
    fun itineraryItemDao_deletingTripCascadesToActivities() = runBlocking {
        val trip = tripEntity(id = "trip-1", ownerUserId = "user-1")
        database.tripDao().insertTrip(trip)
        database.itineraryItemDao().insertItems(
            listOf(
                itineraryItemEntity(id = "item-1", tripId = "trip-1"),
                itineraryItemEntity(id = "item-2", tripId = "trip-1")
            )
        )

        database.tripDao().deleteTrip(trip)

        val items = database.itineraryItemDao().getItemsByTrip("trip-1")
        assertTrue(items.isEmpty())
    }

    @Test
    fun userDao_countUsersByUsernameExcludingUserId_detectsDuplicateUsername() = runBlocking {
        database.userDao().insertUser(userEntity(id = "user-1", username = "duplicate"))
        database.userDao().insertUser(userEntity(id = "user-2", username = "other"))

        val duplicateCount = database.userDao()
            .countUsersByUsernameExcludingUserId(username = "duplicate", userId = "user-2")
        val ownUsernameCount = database.userDao()
            .countUsersByUsernameExcludingUserId(username = "other", userId = "user-2")

        assertEquals(1, duplicateCount)
        assertEquals(0, ownUsernameCount)
    }

    @Test
    fun accessLogDao_getLogsByUserId_filtersAndOrdersLogs() = runBlocking {
        database.accessLogDao().insertLog(
            AccessLogEntity(
                userId = "user-1",
                eventType = "LOGIN",
                occurredAtEpochMillis = 1_000L
            )
        )
        database.accessLogDao().insertLog(
            AccessLogEntity(
                userId = "user-2",
                eventType = "LOGIN",
                occurredAtEpochMillis = 500L
            )
        )
        database.accessLogDao().insertLog(
            AccessLogEntity(
                userId = "user-1",
                eventType = "LOGOUT",
                occurredAtEpochMillis = 2_000L
            )
        )

        val logs = database.accessLogDao().getLogsByUserId("user-1")

        assertEquals(2, logs.size)
        assertEquals(listOf("LOGIN", "LOGOUT"), logs.map { it.eventType })
        assertTrue(logs.all { it.userId == "user-1" })
    }

    private fun tripEntity(
        id: String,
        ownerUserId: String,
        title: String = "Trip",
        startDateEpochMillis: Long = 1_000L
    ) = TripEntity(
        id = id,
        title = title,
        destination = "Barcelona",
        ownerUserId = ownerUserId,
        startDateEpochMillis = startDateEpochMillis,
        endDateEpochMillis = startDateEpochMillis + 1_000L,
        description = "Test trip",
        budgetAmount = 300,
        budgetCurrency = "EUR",
        emoji = "✈️"
    )

    private fun itineraryItemEntity(
        id: String,
        tripId: String
    ) = ItineraryItemEntity(
        id = id,
        tripId = tripId,
        title = "Activity",
        description = "Test activity",
        scheduledAtEpochMillis = 1_000L,
        durationMinutes = 60,
        displayOrder = 1,
        isDone = false
    )

    private fun userEntity(
        id: String,
        username: String
    ) = UserEntity(
        id = id,
        email = "$id@example.com",
        username = username,
        birthDate = Date(1_000L),
        address = "Street 1",
        country = "Spain",
        phone = "123456789",
        acceptsMarketingEmails = false
    )
}
