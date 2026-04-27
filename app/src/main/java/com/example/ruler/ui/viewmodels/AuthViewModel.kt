package com.example.ruler.ui.viewmodels

import android.util.Log
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

    private val tag = "AuthViewModel"

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        Log.i(tag, "ViewModel inicialitzat")
        refreshSession()
    }

    fun refreshSession() {
        Log.d(tag, "refreshSession: comprovant estat de sessió")
        val sessionState = when {
            !authRepository.isFirebaseConfigured() -> {
                Log.e(tag, "refreshSession: Firebase no configurat")
                AuthSessionState.FirebaseNotConfigured
            }
            authRepository.hasAuthenticatedUser() -> {
                Log.i(tag, "refreshSession: usuari autenticat")
                AuthSessionState.Authenticated
            }
            else -> {
                Log.i(tag, "refreshSession: cal login")
                AuthSessionState.RequiresLogin
            }
        }
        _uiState.value = AuthUiState(sessionState = sessionState)
    }

    fun signIn(email: String, password: String) {
        Log.i(tag, "signIn: iniciant login per email=$email")
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = authRepository.signIn(email.trim(), password)
            _uiState.value = if (result.isSuccess) {
                Log.i(tag, "signIn: login exitós")
                AuthUiState(sessionState = AuthSessionState.Authenticated)
            } else {
                val error = result.exceptionOrNull()?.localizedMessage
                Log.e(tag, "signIn: error → $error")
                val sessionState = if (authRepository.isFirebaseConfigured()) {
                    AuthSessionState.RequiresLogin
                } else {
                    AuthSessionState.FirebaseNotConfigured
                }
                AuthUiState(
                    sessionState = sessionState,
                    errorMessage = error
                )
            }
        }
    }

    fun signOut() {
        Log.i(tag, "signOut: tancant sessió")
        authRepository.signOut()
        _uiState.value = AuthUiState(sessionState = AuthSessionState.RequiresLogin)
        Log.i(tag, "signOut: estat actualitzat a RequiresLogin")
    }
}