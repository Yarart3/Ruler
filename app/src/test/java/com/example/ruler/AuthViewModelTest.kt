package com.example.ruler

import android.os.Looper
import com.example.ruler.ui.viewmodels.AuthSessionState
import com.example.ruler.ui.viewmodels.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthViewModelTest {

    @Test
    fun signIn_recordsLoginEvent() {
        val authRepository = FakeAuthRepository(
            userId = null,
            email = null,
            username = null
        )
        val accessLogRepository = FakeAccessLogRepository()
        val viewModel = AuthViewModel(authRepository, accessLogRepository)

        authRepository.setCurrentUser("user-1", "user1@example.com", "user1")
        viewModel.signIn("user1@example.com", "secret")
        shadowOf(Looper.getMainLooper()).idle()

        val logs = accessLogRepository.allLogs()
        assertEquals(AuthSessionState.Authenticated, viewModel.uiState.value.sessionState)
        assertEquals(1, logs.size)
        assertEquals("LOGIN", logs.single().eventType)
        assertEquals("user-1", logs.single().userId)
    }

    @Test
    fun signOut_recordsLogoutEventAndClearsSession() {
        val authRepository = FakeAuthRepository()
        val accessLogRepository = FakeAccessLogRepository()
        val viewModel = AuthViewModel(authRepository, accessLogRepository)

        viewModel.signOut()
        shadowOf(Looper.getMainLooper()).idle()

        val logs = accessLogRepository.allLogs()
        assertEquals(AuthSessionState.RequiresLogin, viewModel.uiState.value.sessionState)
        assertTrue(logs.any { it.eventType == "LOGOUT" && it.userId == "user-1" })
        assertEquals(null, authRepository.currentUserId())
    }
}
