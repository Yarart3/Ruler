package com.example.ruler.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.LocalHotelAssignment
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
        if (tripId == null) flowOf(emptyList())
        else repository.observeActivitiesByTrip(tripId)
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
        Log.i(TAG, "ViewModel inicializado")
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
        if (!validateTrip(title, destination, startDate, endDate, description, budget, emoji)) return

        // Validació de títol duplicat (T5.2)
        val titleExists = trips.value.any { it.title.trim().equals(title.trim(), ignoreCase = true) }
        if (titleExists) {
            _errorMessage.value = "A trip with this name already exists"
            Log.e(TAG, "Error al crear viaje: nombre duplicado → '$title'")
            return
        }

        val trip = Trip(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            destination = destination.trim(),
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
                Log.i(TAG, "Viaje creado correctamente: id=${trip.id} title='${trip.title}'")
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error"
                Log.e(TAG, "Error al guardar viaje en BD: ${e.message}", e)
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTrip(id)
                if (selectedTripId.value == id) selectedTripId.value = null
                Log.i(TAG, "Viaje eliminado: id=$id")
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar viaje id=$id: ${e.message}", e)
            }
        }
    }

    fun editTrip(trip: Trip) {
        if (!validateTrip(
                trip.title, trip.destination, trip.startDate,
                trip.endDate, trip.description, trip.budget, trip.emoji
            )
        ) return

        // Validació de títol duplicat excloent el propi viatge (T5.2)
        val titleExists = trips.value.any {
            it.id != trip.id && it.title.trim().equals(trip.title.trim(), ignoreCase = true)
        }
        if (titleExists) {
            _errorMessage.value = "A trip with this name already exists"
            Log.e(TAG, "Error al editar viaje: nombre duplicado → '${trip.title}'")
            return
        }

        viewModelScope.launch {
            try {
                repository.editTrip(trip)
                clearError()
                Log.i(TAG, "Viaje editado correctamente: id=${trip.id} title='${trip.title}'")
            } catch (e: Exception) {
                Log.e(TAG, "Error al editar viaje id=${trip.id}: ${e.message}", e)
            }
        }
    }

    fun selectTrip(id: String) {
        Log.d(TAG, "Viaje seleccionado: id=$id")
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
                _errorMessage.value = "Activity date must be within trip date range (${trip?.startDate} - ${trip?.endDate})"
                Log.e(TAG, "Error al crear actividad: fecha=$date fuera del rango del viaje id=$tripId")
                return@launch
            }

            // Validació de títol duplicat en activitats del mateix viatge (T5.2)
            val activityTitleExists = activities.value.any {
                it.title.trim().equals(title.trim(), ignoreCase = true)
            }
            if (activityTitleExists) {
                _errorMessage.value = "An activity with this name already exists in this trip"
                Log.e(TAG, "Error al crear actividad: nombre duplicado → '$title' en viaje id=$tripId")
                return@launch
            }

            val activity = TripActivity(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                title = title.trim(),
                description = description,
                date = date,
                time = time
            )

            try {
                repository.addActivity(activity)
                clearError()
                Log.i(TAG, "Actividad creada: id=${activity.id} title='${activity.title}' tripId=$tripId")
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar actividad en BD: ${e.message}", e)
            }
        }
    }

    fun getTripById(id: String): Trip? = trips.value.find { it.id == id }

    fun assignHotelToTrip(
        tripId: String,
        hotelId: String,
        hotelName: String,
        hotelAddress: String,
        checkIn: String,
        checkOut: String
    ) {
        viewModelScope.launch {
            val trip = repository.getTripById(tripId) ?: return@launch
            val checkInDate = parseDate(checkIn)
            val checkOutDate = parseDate(checkOut)
            val tripStart = parseDate(trip.startDate)
            val tripEnd = parseDate(trip.endDate)

            if (checkInDate == null || checkOutDate == null || tripStart == null || tripEnd == null) {
                _errorMessage.value = "Invalid date format"
                return@launch
            }
            if (checkInDate.before(tripStart) || checkOutDate.after(tripEnd)) {
                _errorMessage.value = "Hotel dates must be within trip range (${trip.startDate} - ${trip.endDate})"
                return@launch
            }
            if (!checkInDate.before(checkOutDate)) {
                _errorMessage.value = "Check-in must be before check-out"
                return@launch
            }
            repository.editTrip(
                trip.copy(
                    localHotel = LocalHotelAssignment(
                        hotelId = hotelId,
                        hotelName = hotelName,
                        hotelAddress = hotelAddress,
                        checkInDate = checkIn,
                        checkOutDate = checkOut
                    )
                )
            )
            clearError()
        }
    }

    fun removeHotelFromTrip(tripId: String) {
        viewModelScope.launch {
            val trip = repository.getTripById(tripId) ?: return@launch
            repository.editTrip(trip.copy(localHotel = null))
        }
    }

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
                Log.e(TAG, "Error al actualizar actividad id=${activity.id}: fecha fuera del rango")
                return@launch
            }

            try {
                repository.updateActivity(activity)
                clearError()
                Log.i(TAG, "Actividad actualizada: id=${activity.id} title='${activity.title}'")
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar actividad id=${activity.id}: ${e.message}", e)
            }
        }
    }

    fun toggleActivityDone(id: String) {
        val activity = activities.value.find { it.id == id } ?: return
        viewModelScope.launch {
            try {
                repository.updateActivity(activity.copy(isDone = !activity.isDone))
                Log.i(TAG, "Actividad marcada como isDone=${!activity.isDone}: id=$id")
            } catch (e: Exception) {
                Log.e(TAG, "Error al cambiar estado actividad id=$id: ${e.message}", e)
            }
        }
    }

    fun deleteActivity(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteActivity(id)
                Log.i(TAG, "Actividad eliminada: id=$id")
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar actividad id=$id: ${e.message}", e)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshSessionData() {
        Log.d(TAG, "Refrescando datos de sesión")
        sessionRefreshTrigger.value += 1
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun clearSessionData() {
        Log.d(TAG, "Limpiando datos de sesión")
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
        // Camps obligatoris (T5.2)
        if (title.isBlank() || destination.isBlank() || startDate.isBlank() ||
            endDate.isBlank() || description.isBlank() || budget.isBlank() || emoji.isBlank()
        ) {
            _errorMessage.value = "All fields are required"
            Log.e(TAG, "Error de validación: campos obligatorios vacíos")
            return false
        }

        // Longitud mínima del títol (T5.2)
        if (title.trim().length < 3) {
            _errorMessage.value = "Trip name must be at least 3 characters"
            Log.e(TAG, "Error de validación: nombre demasiado corto → '$title'")
            return false
        }

        // Validació de dates (T5.2)
        val parsedStartDate = parseDate(startDate)
        val parsedEndDate = parseDate(endDate)

        if (parsedStartDate == null || parsedEndDate == null) {
            _errorMessage.value = "Invalid date format (dd/MM/yyyy)"
            Log.e(TAG, "Error de validación: formato de fecha incorrecto → startDate=$startDate endDate=$endDate")
            return false
        }

        if (!parsedStartDate.before(parsedEndDate)) {
            _errorMessage.value = "Start date must be before end date"
            Log.e(TAG, "Error de validación: fecha inicio >= fecha fin → $startDate >= $endDate")
            return false
        }

        Log.d(TAG, "Validación de viaje correcta: title='$title' $startDate → $endDate")
        return true
    }

    private fun parseDate(value: String) = runCatching {
        dateFormat.parse(value)
    }.getOrNull()

    companion object {
        private const val TAG = "TripListViewModel"
    }
}