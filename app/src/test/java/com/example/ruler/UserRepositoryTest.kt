package com.example.ruler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.repository.UserRepositoryImpl
import com.example.ruler.data.repository.toEntity
import com.example.ruler.domain.UserProfile
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserRepositoryTest {

    private lateinit var database: RulerDatabase
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = UserRepositoryImpl(
            userDao = database.userDao(),
            authRepository = FakeAuthRepository(
                userId = "user-1",
                email = "user1@example.com",
                username = "user1"
            )
        )
        repository.syncCurrentUser()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun syncCurrentUser_createsLocalUserProfile() = runBlocking {
        val user = repository.getCurrentUser()

        assertNotNull(user)
        assertEquals("user1@example.com", user?.email)
        assertEquals("user1", user?.username)
    }

    @Test
    fun saveCurrentUserProfile_rejectsDuplicatedUsername() = runBlocking {
        database.userDao().insertUser(
            UserProfile(
                id = "user-2",
                email = "user2@example.com",
                username = "duplicate",
                birthDate = "",
                address = "",
                country = "",
                phone = "",
                acceptsMarketingEmails = false
            ).toEntity()
        )

        val result = runCatching {
            repository.saveCurrentUserProfile(
                UserProfile(
                    id = "user-1",
                    email = "user1@example.com",
                    username = "duplicate",
                    birthDate = "",
                    address = "",
                    country = "",
                    phone = "",
                    acceptsMarketingEmails = false
                )
            )
        }

        if (result.isSuccess) {
            throw AssertionError("Expected duplicated username validation to fail")
        }

        val error = result.exceptionOrNull()
        assertEquals("Username is already in use", error?.message)
    }
}
