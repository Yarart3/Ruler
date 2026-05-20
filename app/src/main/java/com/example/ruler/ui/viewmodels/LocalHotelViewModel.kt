package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.LocalHotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LocalHotelViewModel @Inject constructor(
    private val repository: LocalHotelRepository
) : ViewModel() {

    val hotels: StateFlow<List<LocalHotel>> = repository.observeHotels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addHotel(name: String, address: String, nights: Int = 0, pricePerNight: Double = 0.0): String {
        val id = UUID.randomUUID().toString()
        if (name.isBlank()) return id
        viewModelScope.launch {
            repository.addHotel(
                LocalHotel(
                    id = id,
                    name = name.trim(),
                    address = address.trim(),
                    nights = nights,
                    pricePerNight = pricePerNight
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
        viewModelScope.launch { repository.deleteHotel(id) }
    }
}
