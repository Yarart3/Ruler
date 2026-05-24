package com.example.ruler.data.repository

import com.example.ruler.data.local.dao.LocalHotelDao
import com.example.ruler.data.local.entity.LocalHotelEntity
import com.example.ruler.domain.AuthRepository
import com.example.ruler.domain.LocalHotel
import com.example.ruler.domain.LocalHotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalHotelRepositoryImpl @Inject constructor(
    private val localHotelDao: LocalHotelDao,
    private val authRepository: AuthRepository
) : LocalHotelRepository {

    override fun observeHotels(): Flow<List<LocalHotel>> {
        val userId = authRepository.currentUserId() ?: return flowOf(emptyList())
        return localHotelDao.observeHotelsByOwner(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getHotelById(id: String): LocalHotel? =
        localHotelDao.getHotelById(id)?.toDomain()

    override suspend fun addHotel(hotel: LocalHotel) {
        val userId = authRepository.currentUserId() ?: return
        localHotelDao.insertHotel(hotel.toEntity(userId))
    }

    override suspend fun updateHotel(hotel: LocalHotel) {
        val userId = authRepository.currentUserId() ?: return
        localHotelDao.updateHotel(hotel.toEntity(userId))
    }

    override suspend fun deleteHotel(id: String) {
        localHotelDao.deleteHotelById(id)
    }

    private fun LocalHotel.toEntity(userId: String) = LocalHotelEntity(
        id = id,
        name = name,
        address = address,
        ownerUserId = userId,
        nights = nights,
        pricePerNight = pricePerNight,
        assignedTripId = assignedTripId,
        reservationId = reservationId,
        remoteHotelId = remoteHotelId,
        remoteRoomId = remoteRoomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotelImageUrl = hotelImageUrl,
        roomImageUrls = roomImageUrls.joinToString(",").ifEmpty { null }
    )

    private fun LocalHotelEntity.toDomain() = LocalHotel(
        id = id,
        name = name,
        address = address,
        nights = nights,
        pricePerNight = pricePerNight,
        assignedTripId = assignedTripId,
        reservationId = reservationId,
        remoteHotelId = remoteHotelId,
        remoteRoomId = remoteRoomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotelImageUrl = hotelImageUrl,
        roomImageUrls = roomImageUrls?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    )
}
