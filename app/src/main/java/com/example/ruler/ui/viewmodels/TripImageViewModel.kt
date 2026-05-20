package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.TripImage
import com.example.ruler.domain.TripImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripImageViewModel @Inject constructor(
    private val repository: TripImageRepository
) : ViewModel() {

    private val _tripId = MutableStateFlow("")

    val images: StateFlow<List<TripImage>> = _tripId
        .filter { it.isNotEmpty() }
        .flatMapLatest { repository.getImagesForTrip(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTripId(tripId: String) {
        _tripId.value = tripId
    }

    fun addImages(tripId: String, uris: List<String>) {
        viewModelScope.launch {
            repository.addImages(tripId, uris)
        }
    }

    fun deleteImage(imageId: String) {
        viewModelScope.launch {
            repository.deleteImage(imageId)
        }
    }
}
