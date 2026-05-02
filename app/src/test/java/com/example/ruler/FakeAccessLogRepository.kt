package com.example.ruler

import com.example.ruler.domain.AccessLogEntry
import com.example.ruler.domain.AccessLogRepository

class FakeAccessLogRepository : AccessLogRepository {
    private val logs = mutableListOf<AccessLogEntry>()

    override suspend fun recordLogin(userId: String) {
        logs += AccessLogEntry(
            id = logs.size.toLong() + 1,
            userId = userId,
            eventType = "LOGIN",
            occurredAtEpochMillis = System.currentTimeMillis()
        )
    }

    override suspend fun recordLogout(userId: String) {
        logs += AccessLogEntry(
            id = logs.size.toLong() + 1,
            userId = userId,
            eventType = "LOGOUT",
            occurredAtEpochMillis = System.currentTimeMillis()
        )
    }

    override suspend fun getLogsByUserId(userId: String): List<AccessLogEntry> {
        return logs.filter { it.userId == userId }
    }

    fun allLogs(): List<AccessLogEntry> = logs.toList()
}
