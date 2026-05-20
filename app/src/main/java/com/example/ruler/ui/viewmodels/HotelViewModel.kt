package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelRepository
import com.example.ruler.domain.HotelReservationDetails
import com.example.ruler.domain.HotelReservation
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelReservationResult
import com.example.ruler.domain.HotelRoom
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HotelUiState(
    val isLoading: Boolean = false,
    val hotels: List<Hotel> = emptyList(),
    val reservations: List<HotelReservation> = emptyList(),
    val lastReservation: HotelReservationResult? = null,
    val createdTripId: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelUiState())
    val uiState: StateFlow<HotelUiState> = _uiState.asStateFlow()

    fun listHotels() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
        viewModelScope.launch {
            hotelRepository.listHotels()
                .onSuccess { hotels ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hotels = hotels
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to load hotels"
                    )
                }
        }
    }

    fun searchAvailability(
        startDate: String,
        endDate: String,
        hotelId: String? = null,
        city: String? = null
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
        viewModelScope.launch {
            hotelRepository.checkAvailability(startDate, endDate, hotelId, city)
                .onSuccess { availability ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hotels = availability.availableHotels
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to check availability"
                    )
                }
        }
    }

    fun reserveRoom(request: HotelReservationRequest) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
        viewModelScope.launch {
            hotelRepository.reserveRoom(request)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastReservation = result,
                        successMessage = result.message
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to reserve room"
                    )
                }
        }
    }

    fun reserveRoomAndCreateTrip(
        hotel: Hotel,
        room: HotelRoom,
        request: HotelReservationRequest
    ) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
        viewModelScope.launch {
            hotelRepository.reserveRoom(request)
                .onSuccess { result ->
                    val trip = result.toTrip(hotel = hotel, room = room)
                    runCatching {
                        tripRepository.addTrip(trip)
                    }.onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            lastReservation = result,
                            createdTripId = trip.id,
                            successMessage = "Reservation confirmed and saved as a new trip"
                        )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            lastReservation = result,
                            errorMessage = error.localizedMessage
                                ?: "Reservation completed, but the trip could not be saved locally"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to reserve room"
                    )
                }
        }
    }

    fun loadReservations(guestEmail: String? = null) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
        viewModelScope.launch {
            hotelRepository.listReservations(guestEmail)
                .onSuccess { reservations ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reservations = reservations
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to load reservations"
                    )
                }
        }
    }

    fun cancelReservation(request: HotelReservationRequest) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
        viewModelScope.launch {
            hotelRepository.cancelReservation(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Reservation cancelled"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to cancel reservation"
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null,
            createdTripId = null
        )
    }
}

private fun HotelReservationResult.toTrip(
    hotel: Hotel,
    room: HotelRoom
): Trip {
    return Trip(
        id = UUID.randomUUID().toString(),
        title = "${hotel.name} stay",
        destination = hotel.address,
        startDate = reservation.startDate.toDisplayDate(),
        endDate = reservation.endDate.toDisplayDate(),
        description = buildString {
            append("Hotel reservation ")
            append(reservation.id)
            append(" · ")
            append(room.roomType.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
            })
            append(" at ")
            append(hotel.name)
        },
        budget = "€${(room.price * nights).toInt()}",
        emoji = "\uD83C\uDFE8",
        hotelReservation = HotelReservationDetails(
            reservationId = reservation.id,
            hotelId = hotel.id,
            hotelName = hotel.name,
            hotelAddress = hotel.address,
            hotelImageUrl = hotel.imageUrl,
            roomId = room.id,
            roomType = room.roomType,
            roomPricePerNight = room.price,
            roomImageUrls = room.imageUrls,
            guestName = reservation.guestName,
            guestEmail = reservation.guestEmail,
            nights = nights
        )
    )
}

private fun String.toDisplayDate(): String {
    return LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}
