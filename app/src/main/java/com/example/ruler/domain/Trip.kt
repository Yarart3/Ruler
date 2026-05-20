package com.example.ruler.domain

data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val budget: String,
    val emoji: String,
    val hotelReservation: HotelReservationDetails? = null,
    val localHotels: List<LocalHotelAssignment> = emptyList()
)

data class LocalHotelAssignment(
    val hotelId: String,
    val hotelName: String,
    val hotelAddress: String,
    val checkInDate: String,
    val checkOutDate: String
)

data class HotelReservationDetails(
    val reservationId: String,
    val hotelId: String,
    val hotelName: String,
    val hotelAddress: String,
    val hotelImageUrl: String,
    val roomId: String,
    val roomType: String,
    val roomPricePerNight: Double,
    val roomImageUrls: List<String>,
    val guestName: String,
    val guestEmail: String,
    val nights: Int
)
