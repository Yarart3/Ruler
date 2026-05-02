package com.example.ruler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.repository.ACCESS_LOG_EVENT_LOGIN
import com.example.ruler.data.repository.ACCESS_LOG_EVENT_LOGOUT
import com.example.ruler.data.repository.AccessLogRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccessLogRepositoryTest {

    private lateinit var database: RulerDatabase
    private lateinit var repository: AccessLogRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AccessLogRepositoryImpl(database.accessLogDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recordLoginAndLogout_persistsOrderedLogsForUser() = runBlocking {
        repository.recordLogin("user-1")
        repository.recordLogout("user-1")

        val logs = repository.getLogsByUserId("user-1")

        assertEquals(2, logs.size)
        assertEquals(ACCESS_LOG_EVENT_LOGIN, logs[0].eventType)
        assertEquals(ACCESS_LOG_EVENT_LOGOUT, logs[1].eventType)
        assertTrue(logs[1].occurredAtEpochMillis >= logs[0].occurredAtEpochMillis)
    }
}
