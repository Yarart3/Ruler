package com.example.ruler.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HotelDto(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    val rooms: List<RoomDto>,
    @SerializedName("image_url")
    val imageUrl: String
)

data class RoomDto(
    val id: String,
    @SerializedName("room_type")
    val roomType: String,
    val price: Double,
    val images: List<String>
)

data class HotelAvailabilityResponseDto(
    @SerializedName("available_hotels")
    val availableHotels: List<HotelDto>
)

data class HotelReservationRequestDto(
    @SerializedName("hotel_id")
    val hotelId: String,
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("guest_name")
    val guestName: String,
    @SerializedName("guest_email")
    val guestEmail: String
)

data class HotelReservationResultDto(
    val message: String,
    val nights: Int,
    val reservation: ReservationDto
)

data class HotelReservationListResponseDto(
    val reservations: List<ReservationDto>
)

data class ReservationDto(
    val id: String,
    @SerializedName("hotel_id")
    val hotelId: String,
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("guest_name")
    val guestName: String,
    @SerializedName("guest_email")
    val guestEmail: String,
    val hotel: ReservedHotelSummaryDto? = null,
    val room: RoomDto? = null
)

data class ReservedHotelSummaryDto(
    val id: String,
    val name: String,
    val address: String,
    val rating: Int,
    @SerializedName("image_url")
    val imageUrl: String
)
