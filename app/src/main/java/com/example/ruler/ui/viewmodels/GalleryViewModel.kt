package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripImageRepository
import com.example.ruler.domain.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

data class TripGallerySummary(
    val trip: Trip,
    val imageCount: Int,
    val coverImageUri: String?
)

data class GalleryUiState(
    val tripSummaries: List<TripGallerySummary> = emptyList(),
    val totalPhotos: Int = 0
)

@HiltViewModel
class GalleryViewModel @Inject constructor(
    tripRepository: TripRepository,
    tripImageRepository: TripImageRepository
) : ViewModel() {

    val uiState: StateFlow<GalleryUiState> = combine(
        tripRepository.observeTrips(),
        tripImageRepository.getAllImages()
    ) { trips, images ->
        val imagesByTripId = images.groupBy { it.tripId }
        val summaries = trips
            .map { trip ->
                val tripImages = imagesByTripId[trip.id].orEmpty()
                TripGallerySummary(
                    trip = trip,
                    imageCount = tripImages.size,
                    coverImageUri = tripImages.firstOrNull()?.uri
                )
            }
            .sortedWith(
                compareByDescending<TripGallerySummary> { it.imageCount > 0 }
                    .thenBy { it.trip.startDate }
                    .thenBy { it.trip.title.lowercase() }
            )
        GalleryUiState(
            tripSummaries = summaries,
            totalPhotos = images.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GalleryUiState()
    )
}
