package com.example.ruler.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ruler.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthSessionState {
    Checking,
    RequiresLogin,
    Authenticated,
    FirebaseNotConfigured
}

data class AuthUiState(
    val sessionState: AuthSessionState = AuthSessionState.Checking,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        refreshSession()
    }

    fun refreshSession() {
        val sessionState = when {
            !authRepository.isFirebaseConfigured() -> AuthSessionState.FirebaseNotConfigured
            authRepository.hasAuthenticatedUser() -> AuthSessionState.Authenticated
            else -> AuthSessionState.RequiresLogin
        }

        _uiState.value = AuthUiState(sessionState = sessionState)
    }

    fun signIn(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.signIn(email.trim(), password)
            _uiState.value = if (result.isSuccess) {
                AuthUiState(sessionState = AuthSessionState.Authenticated)
            } else {
                val sessionState = if (authRepository.isFirebaseConfigured()) {
                    AuthSessionState.RequiresLogin
                } else {
                    AuthSessionState.FirebaseNotConfigured
                }

                AuthUiState(
                    sessionState = sessionState,
                    errorMessage = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }
}
