package com.example.ruler.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import com.example.ruler.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TripListViewModel @Inject constructor(
    private val repository: TripRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        isLenient = false
    }

    private val selectedTripId = MutableStateFlow<String?>(null)
    private val sessionRefreshTrigger = MutableStateFlow(0)

    val trips: StateFlow<List<Trip>> = sessionRefreshTrigger.flatMapLatest {
        repository.observeTrips()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val activities: StateFlow<List<TripActivity>> = combine(
        sessionRefreshTrigger,
        selectedTripId
    ) { _, tripId -> tripId }.flatMapLatest { tripId ->
        if (tripId == null) {
            flowOf(emptyList())
        } else {
            repository.observeActivitiesByTrip(tripId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _tripCreatedCounter = MutableStateFlow(0)
    val tripCreatedCounter: StateFlow<Int> = _tripCreatedCounter.asStateFlow()

    init {
        refreshSessionData()
    }

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

        viewModelScope.launch {
            try {
                repository.addTrip(trip)
                clearError()
                _tripCreatedCounter.value += 1
                Log.i(TAG, "Viaje creado correctamente: ${trip.id}")
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error"
                Log.e(TAG, "Error al crear viaje", e)
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            repository.deleteTrip(id)
            if (selectedTripId.value == id) {
                selectedTripId.value = null
            }
            Log.i(TAG, "Viaje eliminado: $id")
        }
    }

    fun editTrip(trip: Trip) {
        if (!validateTrip(
                trip.title,
                trip.destination,
                trip.startDate,
                trip.endDate,
                trip.description,
                trip.budget,
                trip.emoji
            )
        ) {
            return
        }

        viewModelScope.launch {
            repository.editTrip(trip)
            clearError()
            Log.i(TAG, "Viaje editado: ${trip.id}")
        }
    }

    fun selectTrip(id: String) {
        selectedTripId.value = id
    }

    fun addActivity(
        tripId: String,
        title: String,
        description: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch {
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
                return@launch
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
            clearError()
            Log.i(TAG, "Actividad creada correctamente: ${activity.id}")
        }
    }

    fun getTripById(id: String): Trip? = trips.value.find { it.id == id }

    fun updateActivity(activity: TripActivity) {
        viewModelScope.launch {
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
                return@launch
            }

            repository.updateActivity(activity)
            clearError()
            Log.i(TAG, "Actividad actualizada: ${activity.id}")
        }
    }

    fun toggleActivityDone(id: String) {
        val activity = activities.value.find { it.id == id } ?: return
        viewModelScope.launch {
            repository.updateActivity(activity.copy(isDone = !activity.isDone))
            Log.i(TAG, "Estado de actividad actualizado: $id")
        }
    }

    fun deleteActivity(id: String) {
        viewModelScope.launch {
            repository.deleteActivity(id)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshSessionData() {
        sessionRefreshTrigger.value += 1
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun clearSessionData() {
        selectedTripId.value = null
        clearError()
        sessionRefreshTrigger.value += 1
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
        if (
            title.isBlank() ||
            destination.isBlank() ||
            startDate.isBlank() ||
            endDate.isBlank() ||
            description.isBlank() ||
            budget.isBlank() ||
            emoji.isBlank()
        ) {
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

    private fun parseDate(value: String) = runCatching {
        dateFormat.parse(value)
    }.getOrNull()

    companion object {
        private const val TAG = "TripListViewModel"
    }
}
