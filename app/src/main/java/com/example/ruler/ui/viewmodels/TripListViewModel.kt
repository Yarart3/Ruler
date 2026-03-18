package com.example.ruler.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ruler.data.repository.TripRepositoryImpl
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TripListViewModel : ViewModel() {

    private val repository = TripRepositoryImpl()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        isLenient = false
    }

    private val _trips = MutableStateFlow(repository.getTrips())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _activities = MutableStateFlow<List<TripActivity>>(emptyList())
    val activities: StateFlow<List<TripActivity>> = _activities.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var selectedTripId: String? = null

    fun addTrip(
        title: String,
        destination: String,
        startDate: String,
        endDate: String,
        description: String,
        budget: String,
        emoji: String
    ) {
        if (!validateTrip(title, destination, startDate, endDate, description, budget, emoji)) {
            return
        }

        val trip = Trip(
            id = UUID.randomUUID().toString(),
            title = title,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            description = description,
            budget = budget,
            emoji = emoji
        )

        try {
            repository.addTrip(trip)
            refreshTrips()
            clearError()
            Log.i(TAG, "Viaje creado correctamente: ${trip.id}")
        } catch (e: Exception) {
            _errorMessage.value = e.message ?: "Unexpected error"
            Log.e(TAG, "Error al crear viaje", e)
        }
    }

    fun deleteTrip(id: String) {
        repository.deleteTrip(id)
        refreshTrips()
        if (selectedTripId == id) {
            selectedTripId = null
            _activities.value = emptyList()
        }
        Log.i(TAG, "Viaje eliminado: $id")
    }

    fun editTrip(trip: Trip) {
        if (!validateTrip(trip.title, trip.destination, trip.startDate, trip.endDate, trip.description, trip.budget, trip.emoji)) {
            return
        }
        repository.editTrip(trip)
        refreshTrips()
        clearError()
        Log.i(TAG, "Viaje editado: ${trip.id}")
    }

    fun selectTrip(id: String) {
        selectedTripId = id
        _activities.value = repository.getActivitiesByTrip(id)
    }

    fun addActivity(
        tripId: String,
        title: String,
        description: String,
        date: String,
        time: String
    ) {
        val trip = repository.getTripById(tripId)
        val activityDate = parseDate(date)
        val tripStartDate = trip?.let { parseDate(it.startDate) }
        val tripEndDate = trip?.let { parseDate(it.endDate) }

        val isDateInRange = trip != null &&
            activityDate != null &&
            tripStartDate != null &&
            tripEndDate != null &&
            !activityDate.before(tripStartDate) &&
            !activityDate.after(tripEndDate)

        if (!isDateInRange) {
            _errorMessage.value = "Activity date must be within trip date range"
            Log.e(TAG, "Error al crear actividad: fecha fuera del rango del viaje")
            return
        }

        val activity = TripActivity(
            id = UUID.randomUUID().toString(),
            tripId = tripId,
            title = title,
            description = description,
            date = date,
            time = time
        )

        repository.addActivity(activity)
        if (selectedTripId == tripId) {
            _activities.value = repository.getActivitiesByTrip(tripId)
        }
        clearError()
        Log.i(TAG, "Actividad creada correctamente: ${activity.id}")
    }


    fun getTripById(id: String): Trip? = repository.getTripById(id)

    fun updateActivity(activity: TripActivity) {
        val trip = repository.getTripById(activity.tripId)
        val activityDate = parseDate(activity.date)
        val tripStartDate = trip?.let { parseDate(it.startDate) }
        val tripEndDate = trip?.let { parseDate(it.endDate) }

        val isDateInRange = trip != null &&
                activityDate != null &&
                tripStartDate != null &&
                tripEndDate != null &&
                !activityDate.before(tripStartDate) &&
                !activityDate.after(tripEndDate)

        if (!isDateInRange) {
            _errorMessage.value = "Activity date must be within trip date range"
            Log.e(TAG, "Error al actualizar actividad: fecha fuera del rango del viaje")
            return
        }

        repository.updateActivity(activity)
        selectedTripId?.let { _activities.value = repository.getActivitiesByTrip(it) }
        clearError()
        Log.i(TAG, "Actividad actualizada: ${activity.id}")
    }
    fun deleteActivity(id: String) {
        repository.deleteActivity(id)
        selectedTripId?.let { _activities.value = repository.getActivitiesByTrip(it) }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun validateTrip(
        title: String,
        destination: String,
        startDate: String,
        endDate: String,
        description: String,
        budget: String,
        emoji: String
    ): Boolean {
        if (title.isBlank() || destination.isBlank() || startDate.isBlank() || endDate.isBlank() || description.isBlank() || budget.isBlank() || emoji.isBlank()) {
            _errorMessage.value = "All fields are required"
            Log.e(TAG, "Error al guardar viaje: faltan campos obligatorios")
            return false
        }

        val parsedStartDate = parseDate(startDate)
        val parsedEndDate = parseDate(endDate)

        if (parsedStartDate == null || parsedEndDate == null || !parsedStartDate.before(parsedEndDate)) {
            _errorMessage.value = "Start date must be before end date"
            Log.e(TAG, "Error al guardar viaje: rango de fechas invalido")
            return false
        }

        return true
    }

    private fun refreshTrips() {
        _trips.value = repository.getTrips()
    }

    private fun parseDate(value: String) = runCatching {
        dateFormat.parse(value)
    }.getOrNull()

    companion object {
        private const val TAG = "TripListViewModel"
    }
}
