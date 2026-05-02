package com.example.ruler.domain

data class AccessLogEntry(
    val id: Long = 0,
    val userId: String,
    val eventType: String,
    val occurredAtEpochMillis: Long
)

interface AccessLogRepository {
    suspend fun recordLogin(userId: String)

    suspend fun recordLogout(userId: String)

    suspend fun getLogsByUserId(userId: String): List<AccessLogEntry>
}
