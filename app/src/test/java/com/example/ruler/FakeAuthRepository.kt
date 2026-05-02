package com.example.ruler

import com.example.ruler.domain.AuthRepository

class FakeAuthRepository(
    private val userId: String = "user-1",
    private val email: String = "user1@example.com",
    private val username: String = "user1"
) : AuthRepository {
    override fun hasAuthenticatedUser(): Boolean = true

    override fun isFirebaseConfigured(): Boolean = true

    override fun currentUserId(): String = userId

    override fun currentUserEmail(): String = email

    override fun currentUsername(): String = username

    override fun signOut() = Unit

    override suspend fun signIn(email: String, password: String): Result<Unit> = Result.success(Unit)

    override suspend fun register(username: String, email: String, password: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)
}
