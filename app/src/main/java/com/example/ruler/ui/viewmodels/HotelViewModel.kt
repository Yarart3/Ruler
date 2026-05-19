package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelRepository
import com.example.ruler.domain.HotelReservation
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelReservationResult
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelUiState())
    val uiState: StateFlow<HotelUiState> = _uiState.asStateFlow()

    fun listHotels() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
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
            successMessage = null
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
            successMessage = null
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

    fun loadReservations(guestEmail: String? = null) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
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
            successMessage = null
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
            successMessage = null
        )
    }
}
