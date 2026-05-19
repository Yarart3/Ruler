package com.example.ruler.data.remote.api

import com.example.ruler.data.remote.dto.HotelAvailabilityResponseDto
import com.example.ruler.data.remote.dto.HotelDto
import com.example.ruler.data.remote.dto.HotelReservationListResponseDto
import com.example.ruler.data.remote.dto.HotelReservationRequestDto
import com.example.ruler.data.remote.dto.HotelReservationResultDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {

    @GET("hotels/{group_id}/hotels")
    suspend fun listHotels(
        @Path("group_id") groupId: String
    ): List<HotelDto>

    @GET("hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id") groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("hotel_id") hotelId: String? = null,
        @Query("city") city: String? = null
    ): HotelAvailabilityResponseDto

    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: HotelReservationRequestDto
    ): HotelReservationResultDto

    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body request: HotelReservationRequestDto
    ): Unit

    @GET("hotels/{group_id}/reservations")
    suspend fun listReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): HotelReservationListResponseDto
}
