package com.example.ruler.domain

interface HotelRepository {

    suspend fun listHotels(): Result<List<Hotel>>

    suspend fun checkAvailability(
        startDate: String,
        endDate: String,
        hotelId: String? = null,
        city: String? = null
    ): Result<HotelAvailability>

    suspend fun reserveRoom(request: HotelReservationRequest): Result<HotelReservationResult>

    suspend fun cancelReservation(request: HotelReservationRequest): Result<Unit>

    suspend fun listReservations(guestEmail: String? = null): Result<List<HotelReservation>>
}
