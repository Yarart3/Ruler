package com.example.ruler.data.repository

import com.example.ruler.data.local.dao.UserDao
import com.example.ruler.domain.AuthRepository
import com.example.ruler.domain.UserProfile
import com.example.ruler.domain.UserRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val authRepository: AuthRepository
) : UserRepository {

    override fun observeCurrentUser(): Flow<UserProfile?> {
        val currentUserId = authRepository.currentUserId() ?: return flowOf(null)
        return userDao.observeUserById(currentUserId).map { it?.toDomain() }
    }

    override suspend fun getCurrentUser(): UserProfile? {
        val currentUserId = authRepository.currentUserId() ?: return null
        return userDao.getUserById(currentUserId)?.toDomain()
    }

    override suspend fun syncCurrentUser() {
        val currentUserId = authRepository.currentUserId() ?: return
        val currentEmail = authRepository.currentUserEmail().orEmpty()
        val currentUsername = authRepository.currentUsername().takeUnless { it.isNullOrBlank() }
            ?: currentEmail.substringBefore("@")

        val existingUser = userDao.getUserById(currentUserId)
        if (existingUser == null) {
            userDao.insertUser(
                UserProfile(
                    id = currentUserId,
                    email = currentEmail,
                    username = currentUsername,
                    birthDate = "",
                    address = "",
                    country = "",
                    phone = "",
                    acceptsMarketingEmails = false
                ).toEntity()
            )
            return
        }

        if (existingUser.email != currentEmail && currentEmail.isNotBlank()) {
            userDao.updateUser(existingUser.copy(email = currentEmail))
        }
    }

    override suspend fun saveCurrentUserProfile(profile: UserProfile) {
        val currentUserId = authRepository.currentUserId()
            ?: throw IllegalStateException("No authenticated user")
        if (profile.username.isBlank()) {
            throw IllegalArgumentException("Username is required")
        }
        val duplicatedUsers = userDao.countUsersByUsernameExcludingUserId(
            username = profile.username.trim(),
            userId = currentUserId
        )
        if (duplicatedUsers > 0) {
            throw IllegalArgumentException("Username is already in use")
        }

        userDao.insertUser(
            profile.copy(
                id = currentUserId,
                email = authRepository.currentUserEmail().orEmpty(),
                username = profile.username.trim()
            ).toEntity()
        )
    }
}
