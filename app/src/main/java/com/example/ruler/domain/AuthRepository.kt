package com.example.ruler.domain

interface AuthRepository {
    fun hasAuthenticatedUser(): Boolean

    fun isFirebaseConfigured(): Boolean

    suspend fun signIn(email: String, password: String): Result<Unit>
}
