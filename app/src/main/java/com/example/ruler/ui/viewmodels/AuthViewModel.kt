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
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val resetEmailSent: Boolean = false,
    val resetErrorMessage: String? = null
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
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

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

    fun sendPasswordResetEmail(email: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, resetErrorMessage = null, resetEmailSent = false)

        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email.trim())
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false, resetEmailSent = true)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    resetErrorMessage = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun clearResetState() {
        _uiState.value = _uiState.value.copy(resetEmailSent = false, resetErrorMessage = null)
    }
    fun register(username: String, email: String, password: String) {
        Log.i(tag, "register: iniciant registre per email=$email")
        val sessionState = if (authRepository.isFirebaseConfigured()) {
            AuthSessionState.RequiresLogin
        } else {
            AuthSessionState.FirebaseNotConfigured
        }
        _uiState.value = _uiState.value.copy(
            sessionState = sessionState,
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        viewModelScope.launch {
            val result = authRepository.register(username.trim(), email.trim(), password)
            _uiState.value = if (result.isSuccess) {
                Log.i(tag, "register: registre completat per email=$email")
                AuthUiState(
                    sessionState = AuthSessionState.RequiresLogin,
                    successMessage = "Account created. Check your email to verify the account before signing in."
                )
            } else {
                val error = result.exceptionOrNull()?.localizedMessage
                Log.e(tag, "register: error → $error")
                AuthUiState(
                    sessionState = sessionState,
                    errorMessage = error
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun signOut() {
        Log.i(tag, "signOut: tancant sessió")
        authRepository.signOut()
        _uiState.value = AuthUiState(sessionState = AuthSessionState.RequiresLogin)
        Log.i(tag, "signOut: estat actualitzat a RequiresLogin")
    }
}
