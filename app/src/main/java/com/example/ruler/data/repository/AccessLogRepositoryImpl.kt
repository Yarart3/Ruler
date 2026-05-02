package com.example.ruler.data.repository

import com.example.ruler.data.local.dao.AccessLogDao
import com.example.ruler.data.local.entity.AccessLogEntity
import com.example.ruler.domain.AccessLogEntry
import com.example.ruler.domain.AccessLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessLogRepositoryImpl @Inject constructor(
    private val accessLogDao: AccessLogDao
) : AccessLogRepository {

    override suspend fun recordLogin(userId: String) {
        accessLogDao.insertLog(
            AccessLogEntity(
                userId = userId,
                eventType = ACCESS_LOG_EVENT_LOGIN,
                occurredAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun recordLogout(userId: String) {
        accessLogDao.insertLog(
            AccessLogEntity(
                userId = userId,
                eventType = ACCESS_LOG_EVENT_LOGOUT,
                occurredAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getLogsByUserId(userId: String): List<AccessLogEntry> {
        return accessLogDao.getLogsByUserId(userId).map { it.toDomain() }
    }
}
