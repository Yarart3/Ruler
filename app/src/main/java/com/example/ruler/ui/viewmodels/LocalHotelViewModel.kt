package com.example.ruler.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.HotelRepository
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.LocalHotelRepository
import com.example.ruler.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LocalHotelViewModel @Inject constructor(
    private val repository: LocalHotelRepository,
    private val hotelRepository: HotelRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val tag = "LocalHotelViewModel"

    val hotels: StateFlow<List<LocalHotel>> = repository.observeHotels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addHotel(
        name: String,
        address: String,
        nights: Int = 0,
        pricePerNight: Double = 0.0,
        reservationId: String? = null,
        remoteHotelId: String? = null,
        remoteRoomId: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        guestName: String? = null,
        guestEmail: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        if (name.isBlank()) return id
        viewModelScope.launch {
            repository.addHotel(
                LocalHotel(
                    id = id,
                    name = name.trim(),
                    address = address.trim(),
                    nights = nights,
                    pricePerNight = pricePerNight,
                    reservationId = reservationId,
                    remoteHotelId = remoteHotelId,
                    remoteRoomId = remoteRoomId,
                    startDate = startDate,
                    endDate = endDate,
                    guestName = guestName?.trim(),
                    guestEmail = guestEmail?.trim()
                )
            )
        }
        return id
    }

    fun updateHotel(hotel: LocalHotel) {
        if (hotel.name.isBlank()) return
        viewModelScope.launch {
            repository.updateHotel(
                hotel.copy(name = hotel.name.trim(), address = hotel.address.trim())
            )
        }
    }

    fun deleteHotel(id: String) {
        viewModelScope.launch {
            val hotel = repository.getHotelById(id) ?: return@launch
            val reservationId = hotel.reservationId
            val cancelRequest = hotel.remoteHotelId
                ?.takeIf { !hotel.remoteRoomId.isNullOrBlank() }
                ?.takeIf { !hotel.startDate.isNullOrBlank() }
                ?.takeIf { !hotel.endDate.isNullOrBlank() }
                ?.takeIf { !hotel.guestName.isNullOrBlank() }
                ?.takeIf { !hotel.guestEmail.isNullOrBlank() }
                ?.let {
                    HotelReservationRequest(
                        hotelId = hotel.remoteHotelId.orEmpty(),
                        roomId = hotel.remoteRoomId.orEmpty(),
                        startDate = hotel.startDate.orEmpty(),
                        endDate = hotel.endDate.orEmpty(),
                        guestName = hotel.guestName.orEmpty(),
                        guestEmail = hotel.guestEmail.orEmpty()
                    )
                }

            val cancelResult = when {
                !reservationId.isNullOrBlank() -> hotelRepository.cancelReservationById(reservationId)
                cancelRequest != null -> hotelRepository.cancelReservation(cancelRequest)
                else -> null
            }

            if (cancelResult?.isFailure == true) {
                Log.e(tag, "deleteHotel: remote cancellation failed for hotelId=$id", cancelResult.exceptionOrNull())
                return@launch
            }

            val trips = tripRepository.observeTrips().first()
            trips.filter { trip -> trip.localHotels.any { it.hotelId == id } }
                .forEach { trip ->
                    tripRepository.editTrip(
                        trip.copy(localHotels = trip.localHotels.filter { it.hotelId != id })
                    )
                }

            repository.deleteHotel(id)
        }
    }
}
