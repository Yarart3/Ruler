package com.example.ruler

import com.example.ruler.data.fakeDB.FakeTripDataSource
import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TripActivityRepositoryTest {

    private lateinit var repository: TripRepositoryImpl

    @Before
    fun setUp() {
        FakeTripDataSource.trips.clear()
        FakeTripDataSource.activities.clear()
        repository = TripRepositoryImpl()
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

    @Test
    fun addActivity_addsActivityToTrip() {
        val activity = createActivity(id = "activity-1")

        repository.addActivity(activity)

        val activities = repository.getActivitiesByTrip("trip-1")
        assertTrue(activities.contains(activity))
    }

    @Test
    fun updateActivity_updatesActivityData() {
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
    fun deleteActivity_removesActivity() {
        val activity = createActivity(id = "activity-1")
        repository.addActivity(activity)

        repository.deleteActivity(activity.id)

        val activities = repository.getActivitiesByTrip("trip-1")
        assertTrue(activities.none { it.id == activity.id })
    }

    @Test
    fun getActivitiesByTrip_withValidTripId_returnsCorrectActivities() {
        val first = createActivity(id = "activity-1", title = "Breakfast")
        val second = createActivity(id = "activity-2", title = "Walk")
        val otherTripActivity = createActivity(id = "activity-3", tripId = "trip-2", title = "Other")
        FakeTripDataSource.trips.add(
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
