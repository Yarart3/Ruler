package com.example.ruler.data.remote.mapper

import com.example.ruler.data.remote.dto.HotelAvailabilityResponseDto
import com.example.ruler.data.remote.dto.HotelDto
import com.example.ruler.data.remote.dto.HotelReservationListResponseDto
import com.example.ruler.data.remote.dto.HotelReservationRequestDto
import com.example.ruler.data.remote.dto.HotelReservationResultDto
import com.example.ruler.data.remote.dto.ReservationDto
import com.example.ruler.data.remote.dto.ReservedHotelSummaryDto
import com.example.ruler.data.remote.dto.RoomDto
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelAvailability
import com.example.ruler.domain.HotelReservation
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelReservationResult
import com.example.ruler.domain.HotelRoom
import com.example.ruler.domain.ReservedHotelSummary

fun HotelDto.toDomain(baseUrl: String): Hotel {
    return Hotel(
        id = id,
        name = name,
        address = address,
        rating = rating,
        imageUrl = imageUrl.resolveAgainst(baseUrl),
        rooms = rooms.map { it.toDomain(baseUrl) }
    )
}

fun RoomDto.toDomain(baseUrl: String): HotelRoom {
    return HotelRoom(
        id = id,
        roomType = roomType,
        price = price,
        imageUrls = images.map { it.resolveAgainst(baseUrl) }
    )
}

fun HotelAvailabilityResponseDto.toDomain(baseUrl: String): HotelAvailability {
    return HotelAvailability(
        availableHotels = availableHotels.map { it.toDomain(baseUrl) }
    )
}

fun HotelReservationRequest.toDto(): HotelReservationRequestDto {
    return HotelReservationRequestDto(
        hotelId = hotelId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail
    )
}

fun ReservationDto.toDomain(baseUrl: String): HotelReservation {
    return HotelReservation(
        id = id,
        hotelId = hotelId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotel = hotel?.toDomain(baseUrl),
        room = room?.toDomain(baseUrl)
    )
}

fun ReservedHotelSummaryDto.toDomain(baseUrl: String): ReservedHotelSummary {
    return ReservedHotelSummary(
        id = id,
        name = name,
        address = address,
        rating = rating,
        imageUrl = imageUrl.resolveAgainst(baseUrl)
    )
}

fun HotelReservationResultDto.toDomain(baseUrl: String): HotelReservationResult {
    return HotelReservationResult(
        message = message,
        nights = nights,
        reservation = reservation.toDomain(baseUrl)
    )
}

fun HotelReservationListResponseDto.toDomain(baseUrl: String): List<HotelReservation> {
    return reservations.map { it.toDomain(baseUrl) }
}

private fun String.resolveAgainst(baseUrl: String): String {
    return if (startsWith("http://") || startsWith("https://")) {
        this
    } else {
        "${baseUrl.trimEnd('/')}/${trimStart('/')}"
    }
}
