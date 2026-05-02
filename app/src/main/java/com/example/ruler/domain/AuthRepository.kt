package com.example.ruler.domain

interface AuthRepository {
    fun hasAuthenticatedUser(): Boolean

    fun isFirebaseConfigured(): Boolean

    fun currentUserId(): String?

    fun currentUserEmail(): String?

    fun currentUsername(): String?

    fun signOut()

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun register(username: String, email: String, password: String): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
