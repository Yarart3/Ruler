package com.example.ruler.data.repository

import android.util.Log
import com.example.ruler.data.remote.api.HotelApiService
import com.example.ruler.data.remote.mapper.toDomain
import com.example.ruler.data.remote.mapper.toDto
import com.example.ruler.domain.Hotel
import com.example.ruler.domain.HotelAvailability
import com.example.ruler.domain.HotelRepository
import com.example.ruler.domain.HotelReservation
import com.example.ruler.domain.HotelReservationRequest
import com.example.ruler.domain.HotelReservationResult
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApiService,
    @Named("hotelApiGroupId") private val groupId: String,
    @Named("hotelApiBaseUrl") private val baseUrl: String
) : HotelRepository {

    private val tag = "HotelRepository"

    override suspend fun listHotels(): Result<List<Hotel>> = runCatching {
        Log.i(tag, "listHotels: fetching hotels for group=$groupId")
        api.listHotels(groupId).map { it.toDomain(baseUrl) }
    }.also { result ->
        if (result.isFailure) Log.e(tag, "listHotels: error → ${result.exceptionOrNull()?.localizedMessage}")
    }

    override suspend fun checkAvailability(
        startDate: String,
        endDate: String,
        hotelId: String?,
        city: String?
    ): Result<HotelAvailability> = runCatching {
        Log.i(tag, "checkAvailability: start=$startDate end=$endDate hotelId=$hotelId city=$city")
        api.checkAvailability(groupId, startDate, endDate, hotelId, city).toDomain(baseUrl)
    }.also { result ->
        if (result.isFailure) Log.e(tag, "checkAvailability: error → ${result.exceptionOrNull()?.localizedMessage}")
    }

    override suspend fun reserveRoom(request: HotelReservationRequest): Result<HotelReservationResult> = runCatching {
        Log.i(tag, "reserveRoom: hotelId=${request.hotelId} roomId=${request.roomId}")
        api.reserveRoom(groupId, request.toDto()).toDomain(baseUrl)
    }.also { result ->
        if (result.isFailure) Log.e(tag, "reserveRoom: error → ${result.exceptionOrNull()?.localizedMessage}")
    }

    override suspend fun cancelReservation(request: HotelReservationRequest): Result<Unit> = runCatching {
        Log.i(tag, "cancelReservation: hotelId=${request.hotelId} roomId=${request.roomId}")
        api.cancelReservation(groupId, request.toDto())
    }.also { result ->
        if (result.isFailure) Log.e(tag, "cancelReservation: error → ${result.exceptionOrNull()?.localizedMessage}")
    }

    override suspend fun listReservations(guestEmail: String?): Result<List<HotelReservation>> = runCatching {
        Log.i(tag, "listReservations: guestEmail=$guestEmail")
        api.listReservations(groupId, guestEmail).toDomain(baseUrl)
    }.also { result ->
        if (result.isFailure) Log.e(tag, "listReservations: error → ${result.exceptionOrNull()?.localizedMessage}")
    }
}
