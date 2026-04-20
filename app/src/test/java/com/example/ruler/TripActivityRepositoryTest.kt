package com.example.ruler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
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
class TripActivityRepositoryTest {

    private lateinit var database: RulerDatabase
    private lateinit var repository: TripRepositoryImpl

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TripRepositoryImpl(database.tripDao(), database.itineraryItemDao())
        repository.addTrip(
            Trip(
                id = "trip-1",
                title = "Trip",
                destination = "Lisbon, Portugal",
                startDate = "01/05/2026",
                endDate = "05/05/2026",
                description = "Test trip",
                budget = "400",
                emoji = "🌍"
            )
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addActivity_addsActivityToTrip() = runBlocking {
        val activity = createActivity(id = "activity-1")

        repository.addActivity(activity)

        val activities = repository.getActivitiesByTrip("trip-1")
        assertTrue(activities.any { it.id == activity.id })
    }

    @Test
    fun updateActivity_updatesActivityData() = runBlocking {
        val activity = createActivity(id = "activity-1", title = "Museum")
        repository.addActivity(activity)

        repository.updateActivity(
            activity.copy(title = "Dinner", time = "21:00", isDone = true)
        )

        val updatedActivity = repository.getActivitiesByTrip("trip-1").first()
        assertEquals("Dinner", updatedActivity.title)
        assertEquals("21:00", updatedActivity.time)
        assertEquals(true, updatedActivity.isDone)
    }

    @Test
    fun deleteActivity_removesActivity() = runBlocking {
        val activity = createActivity(id = "activity-1")
        repository.addActivity(activity)

        repository.deleteActivity(activity.id)

        val activities = repository.getActivitiesByTrip("trip-1")
        assertTrue(activities.none { it.id == activity.id })
    }

    @Test
    fun getActivitiesByTrip_withValidTripId_returnsCorrectActivities() = runBlocking {
        val first = createActivity(id = "activity-1", title = "Breakfast")
        val second = createActivity(id = "activity-2", title = "Walk")
        val otherTripActivity = createActivity(id = "activity-3", tripId = "trip-2", title = "Other")
        repository.addTrip(
            Trip(
                id = "trip-2",
                title = "Other trip",
                destination = "Paris, France",
                startDate = "06/05/2026",
                endDate = "10/05/2026",
                description = "Other trip",
                budget = "500",
                emoji = "🗼"
            )
        )

        repository.addActivity(first)
        repository.addActivity(second)
        repository.addActivity(otherTripActivity)

        val activities = repository.getActivitiesByTrip("trip-1")

        assertEquals(2, activities.size)
        assertTrue(activities.any { it.id == "activity-1" })
        assertTrue(activities.any { it.id == "activity-2" })
    }

    private fun createActivity(
        id: String,
        tripId: String = "trip-1",
        title: String = "Activity"
    ) = TripActivity(
        id = id,
        tripId = tripId,
        title = title,
        description = "Test activity",
        date = "02/05/2026",
        time = "10:00"
    )
}
