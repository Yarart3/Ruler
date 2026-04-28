package com.example.ruler.domain

interface AuthRepository {
    fun hasAuthenticatedUser(): Boolean

    fun isFirebaseConfigured(): Boolean

    fun signOut()

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun register(email: String, password: String): Result<Unit>
}
