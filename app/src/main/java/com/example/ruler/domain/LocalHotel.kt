package com.example.ruler.domain

data class LocalHotel(
    val id: String,
    val name: String,
    val address: String,
    val nights: Int = 0,
    val pricePerNight: Double = 0.0,
    val assignedTripId: String? = null,
    val reservationId: String? = null,
    val remoteHotelId: String? = null,
    val remoteRoomId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val guestName: String? = null,
    val guestEmail: String? = null,
    val hotelImageUrl: String? = null,
    val roomImageUrls: List<String> = emptyList()
)
