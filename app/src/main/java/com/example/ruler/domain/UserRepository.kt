package com.example.ruler.domain

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<UserProfile?>

    suspend fun getCurrentUser(): UserProfile?

    suspend fun syncCurrentUser()

    suspend fun saveCurrentUserProfile(profile: UserProfile)
}
