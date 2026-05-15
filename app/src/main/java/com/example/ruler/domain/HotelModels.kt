package com.example.ruler.domain

data class Hotel(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val imageUrl: String,
    val rooms: List<HotelRoom>
)

data class HotelRoom(
    val id: String,
    val roomType: String,
    val price: Double,
    val imageUrls: List<String>
)

data class HotelAvailability(
    val availableHotels: List<Hotel>
)

data class HotelReservationRequest(
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String
)

data class HotelReservation(
    val id: String,
    val hotelId: String,
    val roomId: String,
    val startDate: String,
    val endDate: String,
    val guestName: String,
    val guestEmail: String,
    val hotel: ReservedHotelSummary? = null,
    val room: HotelRoom? = null
)

data class ReservedHotelSummary(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val imageUrl: String
)

data class HotelReservationResult(
    val message: String,
    val nights: Int,
    val reservation: HotelReservation
)
