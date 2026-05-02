package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.UserProfile
import com.example.ruler.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfileUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun syncCurrentUser() {
        viewModelScope.launch {
            userRepository.syncCurrentUser()
            _userProfile.value = userRepository.getCurrentUser()
        }
    }

    fun saveProfile(
        username: String,
        birthDate: String,
        address: String,
        country: String,
        phone: String,
        acceptsMarketingEmails: Boolean
    ) {
        val currentProfile = userProfile.value ?: return
        _uiState.value = UserProfileUiState(isSaving = true)
        viewModelScope.launch {
            runCatching {
                userRepository.saveCurrentUserProfile(
                    currentProfile.copy(
                        username = username,
                        birthDate = birthDate,
                        address = address,
                        country = country,
                        phone = phone,
                        acceptsMarketingEmails = acceptsMarketingEmails
                    )
                )
            }.onSuccess {
                _userProfile.value = userRepository.getCurrentUser()
                _uiState.value = UserProfileUiState(successMessage = "Profile saved")
            }.onFailure { error ->
                _uiState.value = UserProfileUiState(
                    errorMessage = error.localizedMessage ?: "Unable to save profile"
                )
            }
        }
    }

    fun clearProfile() {
        _userProfile.value = null
        _uiState.value = UserProfileUiState()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
