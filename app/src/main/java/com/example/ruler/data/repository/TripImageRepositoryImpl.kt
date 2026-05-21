package com.example.ruler.data.repository

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.ruler.data.local.dao.TripImageDao
import com.example.ruler.data.local.entity.TripImageEntity
import com.example.ruler.domain.TripImage
import com.example.ruler.domain.TripImageRepository
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TripImageRepositoryImpl @Inject constructor(
    private val dao: TripImageDao,
    @ApplicationContext private val context: Context
) : TripImageRepository {

    override fun getImagesForTrip(tripId: String): Flow<List<TripImage>> =
        dao.getImagesForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addImages(tripId: String, uris: List<String>) {
        val entities = uris.mapNotNull { sourceUri ->
            copyImageToAppStorage(tripId, sourceUri)
        }.map { localUri ->
            TripImageEntity(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                uri = localUri
            )
        }
        if (entities.isNotEmpty()) {
            dao.insertImages(entities)
        }
    }

    override suspend fun deleteImage(imageId: String) {
        dao.getImageById(imageId)?.let { deleteStoredImage(it.uri) }
        dao.deleteImage(imageId)
    }

    override suspend fun deleteAllForTrip(tripId: String) {
        dao.getImagesForTripSnapshot(tripId).forEach { image ->
            deleteStoredImage(image.uri)
        }
        dao.deleteAllForTrip(tripId)
    }

    private fun TripImageEntity.toDomain() = TripImage(
        id = id,
        tripId = tripId,
        uri = uri,
        addedAt = addedAt
    )

    private fun copyImageToAppStorage(tripId: String, sourceUri: String): String? {
        return runCatching {
            val parsedUri = Uri.parse(sourceUri)
            val extension = context.contentResolver.getType(parsedUri)
                ?.substringAfterLast('/', "")
                ?.takeIf { it.isNotBlank() }
                ?: parsedUri.lastPathSegment
                    ?.substringAfterLast('.', "")
                    ?.takeIf { it.isNotBlank() }
                ?: "jpg"
            val tripDir = File(context.filesDir, "trip_gallery/$tripId").apply { mkdirs() }
            val targetFile = File(tripDir, "${UUID.randomUUID()}.$extension")
            context.contentResolver.openInputStream(parsedUri)?.use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null
            Uri.fromFile(targetFile).toString()
        }.getOrNull()
    }

    private fun deleteStoredImage(storedUri: String) {
        runCatching {
            val parsedUri = Uri.parse(storedUri)
            if (parsedUri.scheme == "file") {
                parsedUri.path?.let(::File)?.delete()
            }
        }
    }
}
