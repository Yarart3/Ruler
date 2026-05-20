package com.example.ruler.data.repository

import com.example.ruler.data.local.dao.TripImageDao
import com.example.ruler.data.local.entity.TripImageEntity
import com.example.ruler.domain.TripImage
import com.example.ruler.domain.TripImageRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TripImageRepositoryImpl @Inject constructor(
    private val dao: TripImageDao
) : TripImageRepository {

    override fun getImagesForTrip(tripId: String): Flow<List<TripImage>> =
        dao.getImagesForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addImages(tripId: String, uris: List<String>) {
        val entities = uris.map { uri ->
            TripImageEntity(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                uri = uri
            )
        }
        dao.insertImages(entities)
    }

    override suspend fun deleteImage(imageId: String) {
        dao.deleteImage(imageId)
    }

    override suspend fun deleteAllForTrip(tripId: String) {
        dao.deleteAllForTrip(tripId)
    }

    private fun TripImageEntity.toDomain() = TripImage(
        id = id,
        tripId = tripId,
        uri = uri,
        addedAt = addedAt
    )
}
