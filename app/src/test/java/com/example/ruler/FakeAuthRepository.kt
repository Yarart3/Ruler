package com.example.ruler

import com.example.ruler.domain.AuthRepository

class FakeAuthRepository(
    private var userId: String? = "user-1",
    private var email: String? = "user1@example.com",
    private var username: String? = "user1",
    private val firebaseConfigured: Boolean = true,
    private var signInResult: Result<Unit> = Result.success(Unit)
) : AuthRepository {
    override fun hasAuthenticatedUser(): Boolean = userId != null

    override fun isFirebaseConfigured(): Boolean = firebaseConfigured

    override fun currentUserId(): String? = userId

    override fun currentUserEmail(): String? = email

    override fun currentUsername(): String? = username

    override fun signOut() {
        userId = null
        email = null
        username = null
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = signInResult

    override suspend fun register(username: String, email: String, password: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)

    fun setCurrentUser(userId: String?, email: String?, username: String?) {
        this.userId = userId
        this.email = email
        this.username = username
    }

    fun setSignInResult(result: Result<Unit>) {
        signInResult = result
    }
}
