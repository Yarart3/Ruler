package com.example.ruler

import com.example.ruler.data.remote.api.HotelApiService
import com.example.ruler.data.remote.dto.HotelAvailabilityResponseDto
import com.example.ruler.data.remote.dto.HotelDto
import com.example.ruler.data.remote.dto.HotelReservationListResponseDto
import com.example.ruler.data.remote.dto.HotelReservationRequestDto
import com.example.ruler.data.remote.dto.HotelReservationResultDto
import com.example.ruler.data.remote.dto.ReservationDto
import com.example.ruler.data.remote.dto.ReservedHotelSummaryDto
import com.example.ruler.data.remote.dto.RoomDto
import com.example.ruler.data.repository.HotelRepositoryImpl
import com.example.ruler.domain.HotelReservationRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HotelRepositoryImplTest {

    private val baseUrl = "http://15.224.84.148:8090/"
    private val groupId = "G02"

    @Test
    fun listHotels_returnsMappedHotelsFromRemoteService() = runBlocking {
        val repository = createRepository(
            FakeHotelApiService(
                hotelsResponse = listOf(
                    HotelDto(
                        id = "hotel-1",
                        name = "Hotel London",
                        address = "1 Baker St",
                        rating = 4,
                        imageUrl = "/images/hotel-1.jpg",
                        rooms = listOf(
                            RoomDto(
                                id = "room-1",
                                roomType = "Double",
                                price = 120.0,
                                images = listOf("/images/room-1.jpg")
                            )
                        )
                    )
                )
            )
        )

        val result = repository.listHotels()

        assertTrue(result.isSuccess)
        val hotels = result.getOrThrow()
        assertEquals(1, hotels.size)
        assertEquals("Hotel London", hotels.single().name)
        assertEquals("${baseUrl}images/hotel-1.jpg", hotels.single().imageUrl)
        assertEquals("${baseUrl}images/room-1.jpg", hotels.single().rooms.single().imageUrls.single())
    }

    @Test
    fun checkAvailability_returnsAvailableHotelsFromRemoteService() = runBlocking {
        val repository = createRepository(
            FakeHotelApiService(
                availabilityResponse = HotelAvailabilityResponseDto(
                    availableHotels = listOf(
                        HotelDto(
                            id = "hotel-2",
                            name = "Hotel Paris",
                            address = "2 Rue de Test",
                            rating = 5,
                            imageUrl = "https://cdn.example.com/hotel-2.jpg",
                            rooms = listOf(
                                RoomDto(
                                    id = "room-2",
                                    roomType = "Suite",
                                    price = 250.0,
                                    images = listOf("https://cdn.example.com/room-2.jpg")
                                )
                            )
                        )
                    )
                )
            )
        )

        val result = repository.checkAvailability(
            startDate = "2026-06-01",
            endDate = "2026-06-05",
            city = "Paris"
        )

        assertTrue(result.isSuccess)
        val hotel = result.getOrThrow().availableHotels.single()
        assertEquals("hotel-2", hotel.id)
        assertEquals("https://cdn.example.com/hotel-2.jpg", hotel.imageUrl)
    }

    @Test
    fun reserveRoom_returnsMappedReservationResult() = runBlocking {
        val repository = createRepository(
            FakeHotelApiService(
                reservationResult = HotelReservationResultDto(
                    message = "Reservation confirmed",
                    nights = 4,
                    reservation = ReservationDto(
                        id = "reservation-1",
                        hotelId = "hotel-1",
                        roomId = "room-1",
                        startDate = "2026-06-01",
                        endDate = "2026-06-05",
                        guestName = "Ada",
                        guestEmail = "ada@example.com",
                        hotel = ReservedHotelSummaryDto(
                            id = "hotel-1",
                            name = "Hotel London",
                            address = "1 Baker St",
                            rating = 4,
                            imageUrl = "/images/hotel-1.jpg"
                        ),
                        room = RoomDto(
                            id = "room-1",
                            roomType = "Double",
                            price = 120.0,
                            images = listOf("/images/room-1.jpg")
                        )
                    )
                )
            )
        )

        val result = repository.reserveRoom(sampleRequest())

        assertTrue(result.isSuccess)
        val reservation = result.getOrThrow()
        assertEquals("Reservation confirmed", reservation.message)
        assertEquals(4, reservation.nights)
        assertEquals("${baseUrl}images/hotel-1.jpg", reservation.reservation.hotel?.imageUrl)
    }

    @Test
    fun listReservations_returnsMappedReservations() = runBlocking {
        val repository = createRepository(
            FakeHotelApiService(
                reservationsResponse = HotelReservationListResponseDto(
                    reservations = listOf(
                        ReservationDto(
                            id = "reservation-2",
                            hotelId = "hotel-3",
                            roomId = "room-3",
                            startDate = "2026-07-10",
                            endDate = "2026-07-12",
                            guestName = "Linus",
                            guestEmail = "linus@example.com",
                            hotel = ReservedHotelSummaryDto(
                                id = "hotel-3",
                                name = "Hotel Barcelona",
                                address = "3 Test Ave",
                                rating = 3,
                                imageUrl = "/images/hotel-3.jpg"
                            ),
                            room = RoomDto(
                                id = "room-3",
                                roomType = "Single",
                                price = 90.0,
                                images = listOf("/images/room-3.jpg")
                            )
                        )
                    )
                )
            )
        )

        val result = repository.listReservations("linus@example.com")

        assertTrue(result.isSuccess)
        val reservation = result.getOrThrow().single()
        assertEquals("reservation-2", reservation.id)
        assertEquals("${baseUrl}images/hotel-3.jpg", reservation.hotel?.imageUrl)
        assertEquals("${baseUrl}images/room-3.jpg", reservation.room?.imageUrls?.single())
    }

    @Test
    fun cancelReservation_returnsSuccessWhenRemoteCallSucceeds() = runBlocking {
        val repository = createRepository(FakeHotelApiService())

        val result = repository.cancelReservation(sampleRequest())

        assertTrue(result.isSuccess)
    }

    @Test
    fun listHotels_returnsFailureWhenRemoteCallFails() = runBlocking {
        val repository = createRepository(
            FakeHotelApiService(
                error = IllegalStateException("Remote unavailable")
            )
        )

        val result = repository.listHotels()

        assertTrue(result.isFailure)
        assertEquals("Remote unavailable", result.exceptionOrNull()?.message)
    }

    private fun createRepository(apiService: HotelApiService): HotelRepositoryImpl {
        return HotelRepositoryImpl(
            api = apiService,
            groupId = groupId,
            baseUrl = baseUrl
        )
    }

    private fun sampleRequest() = HotelReservationRequest(
        hotelId = "hotel-1",
        roomId = "room-1",
        startDate = "2026-06-01",
        endDate = "2026-06-05",
        guestName = "Ada",
        guestEmail = "ada@example.com"
    )
}

private class FakeHotelApiService(
    private val hotelsResponse: List<HotelDto> = emptyList(),
    private val availabilityResponse: HotelAvailabilityResponseDto = HotelAvailabilityResponseDto(emptyList()),
    private val reservationResult: HotelReservationResultDto = HotelReservationResultDto(
        message = "Reservation confirmed",
        nights = 1,
        reservation = ReservationDto(
            id = "reservation-default",
            hotelId = "hotel-default",
            roomId = "room-default",
            startDate = "2026-01-01",
            endDate = "2026-01-02",
            guestName = "Guest",
            guestEmail = "guest@example.com"
        )
    ),
    private val reservationsResponse: HotelReservationListResponseDto = HotelReservationListResponseDto(emptyList()),
    private val error: Throwable? = null
) : HotelApiService {

    override suspend fun listHotels(groupId: String): List<HotelDto> {
        error?.let { throw it }
        return hotelsResponse
    }

    override suspend fun checkAvailability(
        groupId: String,
        startDate: String,
        endDate: String,
        hotelId: String?,
        city: String?
    ): HotelAvailabilityResponseDto {
        error?.let { throw it }
        return availabilityResponse
    }

    override suspend fun reserveRoom(
        groupId: String,
        request: HotelReservationRequestDto
    ): HotelReservationResultDto {
        error?.let { throw it }
        return reservationResult
    }

    override suspend fun cancelReservation(
        groupId: String,
        request: HotelReservationRequestDto
    ) {
        error?.let { throw it }
    }

    override suspend fun listReservations(
        groupId: String,
        guestEmail: String?
    ): HotelReservationListResponseDto {
        error?.let { throw it }
        return reservationsResponse
    }
}
